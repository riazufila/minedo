package net.minedo.mc.repositories.playercolorrepository;

import net.minedo.mc.models.playercolor.PlayerColor;
import net.minedo.mc.repositories.Database;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Player color repository.
 */
public final class PlayerColorRepository {

    private static final Logger logger = Logger.getLogger(PlayerColorRepository.class.getName());

    /**
     * Update player color settings.
     *
     * @param playerUuid  player UUID
     * @param playerColor color settings
     */
    public static void updatePlayerColor(@NotNull UUID playerUuid, @NotNull PlayerColor playerColor) {
        Database database = new Database();
        database.connect();

        String query = """
                    UPDATE player_color
                    SET
                        prefix_preset = ?,
                        prefix_custom = ?,
                        content_preset = ?,
                        content_custom = ?
                    WHERE
                        (player_id = (SELECT id FROM player_profile WHERE uuid = ?));
                """;

        HashMap<Integer, Object> replacements = new HashMap<>();

        replacements.put(1, playerColor.prefixPreset() != null
                ? playerColor.prefixPreset() : null);
        replacements.put(2, playerColor.prefixCustom() != null
                ? playerColor.prefixCustom() : null);
        replacements.put(3, playerColor.contentPreset() != null
                ? playerColor.contentPreset() : null);
        replacements.put(4, playerColor.contentCustom() != null
                ? playerColor.contentCustom() : null);

        replacements.put(5, String.valueOf(playerUuid));
        database.executeStatement(query, replacements);

        database.disconnect();
    }

    /**
     * Insert new player color.
     *
     * @param playerUuid player UUID
     */
    public static void insertNewPlayerColor(@NotNull UUID playerUuid) {
        Database database = new Database();
        database.connect();

        String query = """
                    INSERT INTO player_color (player_id) VALUES ((SELECT id FROM player_profile WHERE uuid = ?));
                """;

        HashMap<Integer, String> replacements = new HashMap<>();
        replacements.put(1, String.valueOf(playerUuid));
        database.executeStatement(query, replacements);

        database.disconnect();
    }

    /**
     * Get player color settings.
     *
     * @param playerUuid player UUID
     * @return color settings
     */
    public static @NotNull PlayerColor getPlayerColorByPlayerUuid(@NotNull UUID playerUuid) {
        Database database = new Database();
        database.connect();

        PlayerColor playerColor = null;

        try {
            String query = """
                        SELECT
                            prefix_preset, prefix_custom, content_preset, content_custom
                        FROM
                            player_color
                        WHERE
                            player_id = (SELECT id FROM player_profile WHERE uuid = ?);
                    """;

            HashMap<Integer, String> replacements = new HashMap<>();
            replacements.put(1, String.valueOf(playerUuid));

            try (ResultSet resultSet = database.queryWithWhereClause(query, replacements)) {
                if (resultSet.next()) {
                    String prefixPreset = resultSet.getString("prefix_preset");
                    String prefixCustom = resultSet.getString("prefix_custom");
                    String contentPreset = resultSet.getString("content_preset");
                    String contentCustom = resultSet.getString("content_custom");

                    playerColor = new PlayerColor(prefixPreset, prefixCustom, contentPreset, contentCustom);
                }
            }
        } catch (SQLException error) {
            logger.severe(String.format("Unable to get player color by UUID: %s", error.getMessage()));
        } finally {
            database.disconnect();
        }

        if (playerColor == null) {
            throw new IllegalStateException("PlayerColor cannot be null.");
        }

        return playerColor;
    }

}
