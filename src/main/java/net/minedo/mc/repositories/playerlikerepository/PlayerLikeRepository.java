package net.minedo.mc.repositories.playerlikerepository;

import net.minedo.mc.models.playerlike.PlayerLike;
import net.minedo.mc.repositories.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

public final class PlayerLikeRepository {

    private static final Logger logger = Logger.getLogger(PlayerLikeRepository.class.getName());

    public static void insertNewPlayerLike(UUID playerUuid) {
        Database database = new Database();
        database.connect();

        String query = """
                    INSERT INTO player_like (player_id) VALUES ((SELECT id FROM player_profile WHERE uuid = ?));
                """;

        HashMap<Integer, String> replacements = new HashMap<>();
        replacements.put(1, String.valueOf(playerUuid));
        database.executeStatement(query, replacements);

        database.disconnect();
    }

    public static void updatePlayerLike(UUID playerUuid, PlayerLike playerLike) {
        Database database = new Database();
        database.connect();

        String query = """
                    UPDATE player_like
                    SET
                        like_received_count = ?,
                        like_sent_count = ?,
                        last_like_sent = ?
                    WHERE
                        (player_id = (SELECT id FROM player_profile WHERE uuid = ?));
                """;

        HashMap<Integer, Object> replacements = new HashMap<>();
        replacements.put(1, String.valueOf(playerLike.likeReceivedCount()));
        replacements.put(2, String.valueOf(playerLike.likeSentCount()));

        replacements.put(
                3, playerLike.lastLikeSent() != null ? Timestamp.from(playerLike.lastLikeSent()) : null
        );

        replacements.put(4, String.valueOf(playerUuid));
        database.executeStatement(query, replacements);

        database.disconnect();
    }

    public static PlayerLike getPlayerLikeByPlayerUuid(UUID playerUuid) {
        Database database = new Database();
        database.connect();

        PlayerLike playerLike = null;

        try {
            String query = """
                        SELECT
                            like_received_count, like_sent_count, last_like_sent
                        FROM
                            player_like WHERE player_id = (SELECT id FROM player_profile WHERE uuid = ?);
                    """;

            HashMap<Integer, String> replacements = new HashMap<>();
            replacements.put(1, String.valueOf(playerUuid));

            try (ResultSet resultSet = database.queryWithWhereClause(query, replacements)) {
                if (resultSet.next()) {
                    int likeReceivedCount = resultSet.getInt("like_received_count");
                    int likeSentCount = resultSet.getInt("like_sent_count");
                    Timestamp lastLikeSent = resultSet.getTimestamp("last_like_sent");

                    playerLike = new PlayerLike(likeReceivedCount, likeSentCount,
                            lastLikeSent != null ? lastLikeSent.toInstant() : null);
                }
            }
        } catch (SQLException error) {
            logger.severe(String.format("Unable to get player color by uuid: %s", error.getMessage()));
        } finally {
            database.disconnect();
        }

        return playerLike;
    }

}
