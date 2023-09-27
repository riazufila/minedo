package net.minedo.mc.repositories.playerprofilerepository;

import net.minedo.mc.models.playerprofile.PlayerProfile;
import net.minedo.mc.repositories.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

public class PlayerProfileRepository {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public void insertNewPlayerProfile(UUID playerUuid) {
        Database database = new Database();
        database.connect();

        String query = """
                    INSERT INTO player_profile (uuid) VALUES (?);
                """;

        HashMap<Integer, String> replacements = new HashMap<>();
        replacements.put(1, playerUuid.toString());
        database.executeStatement(query, replacements);

        database.disconnect();
    }

    public PlayerProfile getPlayerProfileByUuid(UUID playerUuid) {
        Database database = new Database();
        database.connect();

        PlayerProfile playerProfile = null;

        try {
            String query = """
                        SELECT * FROM player_profile WHERE uuid = ?;
                    """;

            HashMap<Integer, String> replacements = new HashMap<>();
            replacements.put(1, playerUuid.toString());
            ResultSet resultSet = database.queryWithWhereClause(query, replacements);

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                String nameColor = resultSet.getString("name_color");
                String nickname = resultSet.getString("nickname");

                playerProfile = new PlayerProfile();
                playerProfile.setId(id);
                playerProfile.setUuid(uuid);
                playerProfile.setNameColor(nameColor);
                playerProfile.setNickname(nickname);
            }
        } catch (SQLException error) {
            this.logger.severe(String.format("Unable to get player profile by uuid: %s", error.getMessage()));
        } finally {
            database.disconnect();
        }

        return playerProfile;
    }

    public PlayerProfile getPlayerProfileById(int playerId) {
        Database database = new Database();
        database.connect();

        PlayerProfile playerProfile = null;

        try {
            String query = """
                        SELECT * FROM player_profile WHERE uuid = ?;
                    """;

            HashMap<Integer, String> replacements = new HashMap<>();
            replacements.put(1, String.valueOf(playerId));
            ResultSet resultSet = database.queryWithWhereClause(query, replacements);

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                String nameColor = resultSet.getString("name_color");
                String nickname = resultSet.getString("nickname");

                playerProfile = new PlayerProfile();
                playerProfile.setId(id);
                playerProfile.setUuid(uuid);
                playerProfile.setNameColor(nameColor);
                playerProfile.setNickname(nickname);
            }
        } catch (SQLException error) {
            this.logger.severe(String.format("Unable to get player profile by id: %s", error.getMessage()));
        } finally {
            database.disconnect();
        }

        return playerProfile;
    }

}
