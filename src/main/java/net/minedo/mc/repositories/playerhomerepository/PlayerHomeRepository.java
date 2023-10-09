package net.minedo.mc.repositories.playerhomerepository;

import net.minedo.mc.models.playerhome.PlayerHome;
import net.minedo.mc.repositories.Database;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Player home repository.
 */
public final class PlayerHomeRepository {

    private static final Logger logger = Logger.getLogger(PlayerHomeRepository.class.getName());

    /**
     * Get player home.
     *
     * @param playerUuid player UUID
     * @param name       home name
     * @return player home
     */
    public static @Nullable PlayerHome getPlayerHome(UUID playerUuid, String name) {
        Database database = new Database();
        database.connect();

        PlayerHome playerHome = null;

        try {
            String query = """
                        SELECT
                            name, world_type, coordinate_x, coordinate_y, coordinate_z
                        FROM
                            player_home
                        WHERE
                            player_id = (SELECT id FROM player_profile WHERE uuid = ?)
                                AND name = ?;
                    """;

            HashMap<Integer, String> replacements = new HashMap<>();
            replacements.put(1, String.valueOf(playerUuid));
            replacements.put(2, name);

            try (ResultSet resultSet = database.queryWithWhereClause(query, replacements)) {
                if (resultSet.next()) {
                    String homeName = resultSet.getString("name");
                    String worldType = resultSet.getString("world_type");
                    double coordinateX = resultSet.getDouble("coordinate_x");
                    double coordinateY = resultSet.getDouble("coordinate_y");
                    double coordinateZ = resultSet.getDouble("coordinate_z");

                    World world = Bukkit.getWorld(worldType);

                    if (world == null) {
                        throw new RuntimeException("World is invalid.");
                    }

                    playerHome = new PlayerHome(homeName, world, coordinateX, coordinateY, coordinateZ);
                }
            }
        } catch (SQLException error) {
            logger.severe(String.format(
                    "Unable to get player home by player UUID and name: %s", error.getMessage()
            ));
        } finally {
            database.disconnect();
        }

        return playerHome;
    }

    /**
     * Get player home list.
     *
     * @param playerUuid player UUID
     * @return player home list
     */
    public static @Nullable List<PlayerHome> getPlayerHomeList(UUID playerUuid) {
        Database database = new Database();
        database.connect();

        List<PlayerHome> playerHomeList = null;

        try {
            String query = """
                        SELECT
                            name, world_type, coordinate_x, coordinate_y, coordinate_z
                        FROM
                            player_home
                        WHERE
                            player_id = (SELECT id FROM player_profile WHERE uuid = ?);
                    """;

            HashMap<Integer, String> replacements = new HashMap<>();
            replacements.put(1, String.valueOf(playerUuid));

            try (ResultSet resultSet = database.queryWithWhereClause(query, replacements)) {
                while (resultSet.next()) {
                    if (playerHomeList == null) {
                        playerHomeList = new ArrayList<>();
                    }

                    String homeName = resultSet.getString("name");
                    String worldType = resultSet.getString("world_type");
                    double coordinateX = resultSet.getDouble("coordinate_x");
                    double coordinateY = resultSet.getDouble("coordinate_y");
                    double coordinateZ = resultSet.getDouble("coordinate_z");

                    World world = Bukkit.getWorld(worldType);

                    if (world == null) {
                        throw new RuntimeException("World is invalid");
                    }

                    PlayerHome playerHome = new PlayerHome(homeName, world, coordinateX, coordinateY, coordinateZ);
                    playerHomeList.add(playerHome);
                }
            }
        } catch (SQLException error) {
            logger.severe(String.format("Unable to get player home: %s", error.getMessage()));
        } finally {
            database.disconnect();
        }

        return playerHomeList;
    }

    /**
     * Upsert player home.
     *
     * @param playerUuid player UUID
     * @param location   home location
     * @param homeName   home name
     */
    public static void upsertHome(UUID playerUuid, Location location, String homeName) {
        Database database = new Database();
        database.connect();

        String query = """
                    REPLACE INTO player_home
                        (player_id, name, world_type, coordinate_x, coordinate_y, coordinate_z)
                    VALUES
                        ((SELECT id FROM player_profile WHERE uuid = ?), ?, ?, ?, ?, ?);
                """;

        HashMap<Integer, String> replacements = new HashMap<>();
        replacements.put(1, String.valueOf(playerUuid));
        replacements.put(2, homeName);
        replacements.put(3, location.getWorld().getName());
        replacements.put(4, String.valueOf(location.getX()));
        replacements.put(5, String.valueOf(location.getY()));
        replacements.put(6, String.valueOf(location.getZ()));
        database.executeStatement(query, replacements);

        database.disconnect();
    }

    /**
     * Remove home from a player.
     *
     * @param playerUuid player UUID
     * @param homeName   home name
     */
    public static void removeHome(UUID playerUuid, String homeName) {
        Database database = new Database();
        database.connect();

        String query = """
                    DELETE FROM
                        player_home
                    WHERE
                        (player_id = (SELECT id FROM player_profile WHERE uuid = ?))
                            AND (name = ?);
                """;

        HashMap<Integer, String> replacements = new HashMap<>();
        replacements.put(1, String.valueOf(playerUuid));
        replacements.put(2, homeName);
        database.executeStatement(query, replacements);

        database.disconnect();
    }

}
