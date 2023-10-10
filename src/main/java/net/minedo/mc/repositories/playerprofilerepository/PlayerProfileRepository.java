package net.minedo.mc.repositories.playerprofilerepository;

import net.minedo.mc.models.playerprofile.PlayerProfile;
import net.minedo.mc.repositories.Database;
import net.minedo.mc.repositories.DatabaseUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Player profile repository.
 */
public final class PlayerProfileRepository {

    private static final Logger logger = Logger.getLogger(PlayerProfileRepository.class.getName());

    /**
     * Insert new player profile.
     *
     * @param playerUuid player UUID
     */
    public static void insertNewPlayerProfile(@NotNull UUID playerUuid) {
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

    /**
     * Get player profile.
     *
     * @param playerUuid player UUID
     * @return player profile
     */
    public static @NotNull PlayerProfile getPlayerProfileByUuid(@NotNull UUID playerUuid) {
        Database database = new Database();
        database.connect();

        PlayerProfile playerProfile = null;

        try {
            String query = """
                        SELECT id, uuid, nickname FROM player_profile WHERE uuid = ?;
                    """;

            HashMap<Integer, String> replacements = new HashMap<>();
            replacements.put(1, playerUuid.toString());

            try (ResultSet resultSet = database.queryWithWhereClause(query, replacements)) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                    String nickname = resultSet.getString("nickname");

                    playerProfile = new PlayerProfile(id, uuid, nickname);
                }
            }
        } catch (SQLException error) {
            logger.severe(String.format("Unable to get player profile by UUID: %s", error.getMessage()));
        } finally {
            database.disconnect();
        }

        if (playerProfile == null) {
            throw new IllegalStateException("PlayerProfile cannot be null.");
        }

        return playerProfile;
    }

    /**
     * Get player profile.
     *
     * @param playerName player nickname
     * @return player profile
     */
    public static @Nullable PlayerProfile getPlayerProfileByNickname(@NotNull String playerName) {
        Database database = new Database();
        database.connect();

        PlayerProfile playerProfile = null;

        try {
            String query = """
                        SELECT id, uuid, nickname FROM player_profile WHERE UPPER(nickname) = ?;
                    """;

            HashMap<Integer, String> replacements = new HashMap<>();
            replacements.put(1, playerName.toUpperCase());

            try (ResultSet resultSet = database.queryWithWhereClause(query, replacements)) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                    String nickname = resultSet.getString("nickname");

                    playerProfile = new PlayerProfile(id, uuid, nickname);
                }
            }
        } catch (SQLException error) {
            logger.severe(String.format("Unable to get player profile by nickname: %s", error.getMessage()));
        } finally {
            database.disconnect();
        }

        return playerProfile;
    }

    /**
     * Update player nickname.
     *
     * @param playerUuid player UUID
     * @param nickname   player nickname
     */
    public static void updatePlayerNickname(@NotNull UUID playerUuid, @Nullable String nickname) {
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

    /**
     * Get all players' nicknames aside from oneself.
     *
     * @param playerUuid UUID of player to exclude from results
     * @return List of nicknames
     */
    public static @Nullable List<String> getOtherPlayersNickname(@NotNull UUID playerUuid) {
        Database database = new Database();
        database.connect();

        List<String> otherPlayersNickname = null;

        try {
            String query = """
                        SELECT
                            nickname
                        FROM
                            player_profile
                        WHERE
                            nickname IS NOT NULL
                                AND uuid != ?;
                    """;

            HashMap<Integer, String> replacements = new HashMap<>();
            replacements.put(1, String.valueOf(playerUuid));

            try (ResultSet resultSet = database.queryWithWhereClause(query, replacements)) {
                while (resultSet.next()) {
                    if (otherPlayersNickname == null) {
                        otherPlayersNickname = new ArrayList<>();
                    }

                    String nickname = resultSet.getString("nickname");
                    otherPlayersNickname.add(nickname);
                }
            }
        } catch (SQLException error) {
            logger.severe(String.format("Unable to get other player nicknames: %s", error.getMessage()));
        } finally {
            database.disconnect();
        }

        return otherPlayersNickname;
    }

    /**
     * Get all players' nicknames aside from oneself within a pool of players.
     *
     * @param playerUuid         UUID of player to exclude from results
     * @param otherOnlinePlayers List of UUID of players to search within
     * @return List of nicknames
     */
    public static @Nullable List<String> getOtherPlayersNickname(
            @NotNull UUID playerUuid, @NotNull List<UUID> otherOnlinePlayers
    ) {
        Database database = new Database();
        database.connect();

        List<String> otherPlayersNickname = null;

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

            try (ResultSet resultSet = database.queryWithWhereClause(query, replacements)) {
                while (resultSet.next()) {
                    if (otherPlayersNickname == null) {
                        otherPlayersNickname = new ArrayList<>();
                    }

                    String nickname = resultSet.getString("nickname");
                    otherPlayersNickname.add(nickname);
                }
            }
        } catch (SQLException error) {
            logger.severe(String.format("Unable to get other player nicknames: %s", error.getMessage()));
        } finally {
            database.disconnect();
        }

        return otherPlayersNickname;
    }

}
