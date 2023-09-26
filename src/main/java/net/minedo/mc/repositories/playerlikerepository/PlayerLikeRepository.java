package net.minedo.mc.repositories.playerlikerepository;

import net.minedo.mc.models.playerlike.PlayerLike;
import net.minedo.mc.repositories.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.logging.Logger;

public class PlayerLikeRepository {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public void insertNewPlayerLike(int playerId) {
        Database database = new Database();
        database.connect();

        String query = """
                    INSERT INTO player_like (player_id) VALUES (?);
                """;

        HashMap<Integer, String> replacements = new HashMap<>();
        replacements.put(1, String.valueOf(playerId));
        database.executeStatement(query, replacements);

        database.disconnect();
    }

    public void updatePlayerLike(int playerId, PlayerLike playerLike) {
        Database database = new Database();
        database.connect();

        String query = """
                    UPDATE player_like
                    SET
                        like_received_count = ?,
                        like_sent_count = ?,
                        last_like_sent = ?
                    WHERE
                        (player_id = ?);
                """;

        HashMap<Integer, Object> replacements = new HashMap<>();
        replacements.put(1, String.valueOf(playerLike.getLikeReceivedCount()));
        replacements.put(2, String.valueOf(playerLike.getLikeSentCount()));

        replacements.put(
                3, playerLike.getLastLikeSent() != null ? Timestamp.from(playerLike.getLastLikeSent()) : null
        );

        replacements.put(4, String.valueOf(playerId));
        database.executeStatement(query, replacements);

        database.disconnect();
    }

    public PlayerLike getPlayerLikeByPlayerId(int playerId) {
        Database database = new Database();
        database.connect();

        PlayerLike playerLike = null;

        try {
            String query = """
                        SELECT * FROM player_like WHERE player_id = ?;
                    """;

            HashMap<Integer, String> replacements = new HashMap<>();
            replacements.put(1, String.valueOf(playerId));
            ResultSet resultSet = database.queryWithWhereClause(query, replacements);

            if (resultSet.next()) {
                int id = resultSet.getInt("player_id");
                int likeReceivedCount = resultSet.getInt("like_received_count");
                int likeSentCount = resultSet.getInt("like_sent_count");
                Timestamp lastLikeSent = resultSet.getTimestamp("last_like_sent");

                playerLike = new PlayerLike();
                playerLike.setPlayerId(id);
                playerLike.setLikeReceivedCount(likeReceivedCount);
                playerLike.setLikeSentCount(likeSentCount);
                playerLike.setLastLikeSent(lastLikeSent != null ? lastLikeSent.toInstant() : null);
            }
        } catch (SQLException error) {
            this.logger.severe(String.format("Unable to get player profile by uuid: %s", error.getMessage()));
        } finally {
            database.disconnect();
        }

        return playerLike;
    }

}
