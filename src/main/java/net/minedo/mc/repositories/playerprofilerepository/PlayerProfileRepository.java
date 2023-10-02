package net.minedo.mc.repositories.playerprofilerepository;

import net.minedo.mc.models.playerprofile.PlayerProfile;
import net.minedo.mc.repositories.Database;
import net.minedo.mc.repositories.DatabaseUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
                String nickname = resultSet.getString("nickname");

                playerProfile = new PlayerProfile();
                playerProfile.setId(id);
                playerProfile.setUuid(uuid);
                playerProfile.setNickname(nickname);
            }
        } catch (SQLException error) {
            this.logger.severe(String.format("Unable to get player profile by uuid: %s", error.getMessage()));
        } finally {
            database.disconnect();
        }

        return playerProfile;
    }

    public PlayerProfile getPlayerProfileByNickname(String playerName) {
        Database database = new Database();
        database.connect();

        PlayerProfile playerProfile = null;

        try {
            String query = """
                        SELECT * FROM player_profile WHERE UPPER(nickname) = ?;
                    """;

            HashMap<Integer, String> replacements = new HashMap<>();
            replacements.put(1, playerName.toUpperCase());
            ResultSet resultSet = database.queryWithWhereClause(query, replacements);

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                String nickname = resultSet.getString("nickname");

                playerProfile = new PlayerProfile();
                playerProfile.setId(id);
                playerProfile.setUuid(uuid);
                playerProfile.setNickname(nickname);
            }
        } catch (SQLException error) {
            this.logger.severe(String.format("Unable to get player profile by nickname: %s", error.getMessage()));
        } finally {
            database.disconnect();
        }

        return playerProfile;
    }

    public void updatePlayerNickname(UUID playerUuid, String nickname) {
        Database database = new Database();
        database.connect();

        String query = """
                    UPDATE player_profile
                    SET
                        nickname = ?
                    WHERE
                        (uuid = ?);
                """;

        HashMap<Integer, Object> replacements = new HashMap<>();
        replacements.put(1, nickname);
        replacements.put(2, String.valueOf(playerUuid));
        database.executeStatement(query, replacements);

        database.disconnect();
    }

    public List<String> getOtherPlayersNickname(UUID playerUuid, List<UUID> otherOnlinePlayers) {
        Database database = new Database();
        database.connect();

        List<String> otherPlayersNickname = new ArrayList<>();

        try {
            String query = String.format("""
                        SELECT
                            nickname
                        FROM
                            player_profile
                        WHERE
                            nickname IS NOT NULL
                                AND uuid != ?
                                AND uuid IN (%s);
                    """, DatabaseUtils.buildWhereInClause(otherOnlinePlayers.size()));

            int queryIndex = 1;

            HashMap<Integer, String> replacements = new HashMap<>();
            replacements.put(queryIndex, String.valueOf(playerUuid));
            queryIndex++;

            for (int i = 0; i < otherOnlinePlayers.size(); i++) {
                replacements.put(queryIndex + i, String.valueOf(otherOnlinePlayers.get(i)));
            }

            ResultSet resultSet = database.queryWithWhereClause(query, replacements);

            if (resultSet.next()) {
                String nickname = resultSet.getString("nickname");
                otherPlayersNickname.add(nickname);
            }
        } catch (SQLException error) {
            this.logger.severe(String.format("Unable to get other player nicknames: %s", error.getMessage()));
        } finally {
            database.disconnect();
        }

        return otherPlayersNickname;
    }

}
