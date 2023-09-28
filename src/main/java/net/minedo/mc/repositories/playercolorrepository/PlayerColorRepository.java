package net.minedo.mc.repositories.playercolorrepository;

import net.minedo.mc.models.playercolor.PlayerColor;
import net.minedo.mc.models.playerprofile.PlayerProfile;
import net.minedo.mc.repositories.Database;
import net.minedo.mc.repositories.playerprofilerepository.PlayerProfileRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

public class PlayerColorRepository {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public void insertNewPlayerColor(UUID playerUuid) {
        Database database = new Database();
        database.connect();

        PlayerProfileRepository playerProfileRepository = new PlayerProfileRepository();
        PlayerProfile playerProfile = playerProfileRepository.getPlayerProfileByUuid(playerUuid);

        String query = """
                    INSERT INTO player_color (player_id) VALUES (?);
                """;

        HashMap<Integer, String> replacements = new HashMap<>();
        replacements.put(1, String.valueOf(playerProfile.getId()));
        database.executeStatement(query, replacements);

        database.disconnect();
    }

    public PlayerColor getPlayerColorByPlayerUuid(UUID playerUuid) {
        Database database = new Database();
        database.connect();

        PlayerProfileRepository playerProfileRepository = new PlayerProfileRepository();
        PlayerProfile playerProfile = playerProfileRepository.getPlayerProfileByUuid(playerUuid);

        PlayerColor playerColor = null;

        try {
            String query = """
                        SELECT * FROM player_color WHERE player_id = ?;
                    """;

            HashMap<Integer, String> replacements = new HashMap<>();
            replacements.put(1, String.valueOf(playerProfile.getId()));
            ResultSet resultSet = database.queryWithWhereClause(query, replacements);

            if (resultSet.next()) {
                int id = resultSet.getInt("player_id");
                String prefixPreset = resultSet.getString("prefix_preset");
                String prefixCustom = resultSet.getString("prefix_custom");
                String contentPreset = resultSet.getString("content_preset");
                String contentCustom = resultSet.getString("content_custom");

                playerColor = new PlayerColor();
                playerColor.setPlayerId(id);
                playerColor.setPrefixPreset(prefixPreset);
                playerColor.setPrefixCustom(prefixCustom);
                playerColor.setContentPreset(contentPreset);
                playerColor.setContentCustom(contentCustom);
            }
        } catch (SQLException error) {
            this.logger.severe(String.format("Unable to get player color by uuid: %s", error.getMessage()));
        } finally {
            database.disconnect();
        }

        return playerColor;
    }

    public void updatePlayerColor(UUID playerUuid, PlayerColor playerColor) {
        Database database = new Database();
        database.connect();

        PlayerProfileRepository playerProfileRepository = new PlayerProfileRepository();
        PlayerProfile playerProfile = playerProfileRepository.getPlayerProfileByUuid(playerUuid);

        String query = """
                    UPDATE player_color
                    SET
                        prefix_preset = ?,
                        prefix_custom = ?,
                        content_preset = ?,
                        content_custom = ?,
                    WHERE
                        (player_id = ?);
                """;

        HashMap<Integer, Object> replacements = new HashMap<>();
        replacements.put(1, String.valueOf(playerColor.getPrefixPreset()));
        replacements.put(2, String.valueOf(playerColor.getPrefixCustom()));
        replacements.put(3, String.valueOf(playerColor.getContentPreset()));
        replacements.put(4, String.valueOf(playerColor.getContentCustom()));
        replacements.put(5, String.valueOf(playerProfile.getId()));
        database.executeStatement(query, replacements);

        database.disconnect();
    }

}
