package net.minedo.mc.repositories.playerhomerepository;

import net.minedo.mc.models.playerhome.PlayerHome;
import net.minedo.mc.models.playerprofile.PlayerProfile;
import net.minedo.mc.repositories.Database;
import net.minedo.mc.repositories.playerprofilerepository.PlayerProfileRepository;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public final class PlayerHomeRepository {

    private static final Logger logger = Logger.getLogger(PlayerHomeRepository.class.getName());

    public static PlayerHome getPlayerHome(UUID playerUuid, String name) {
        Database database = new Database();
        database.connect();

        PlayerProfile playerProfile = PlayerProfileRepository.getPlayerProfileByUuid(playerUuid);
        PlayerHome playerHome = null;

        try {
            String query = """
                        SELECT
                            *
                        FROM
                            player_home
                        WHERE
                            player_id = (SELECT id FROM player_profile WHERE uuid = ?)
                                AND name = ?;
                    """;

            HashMap<Integer, String> replacements = new HashMap<>();
            replacements.put(1, String.valueOf(playerProfile.id()));
            replacements.put(2, name);
            ResultSet resultSet = database.queryWithWhereClause(query, replacements);

            if (resultSet.next()) {
                String homeName = resultSet.getString("name");
                String world = resultSet.getString("world_type");
                double coordinateX = resultSet.getDouble("coordinate_x");
                double coordinateY = resultSet.getDouble("coordinate_y");
                double coordinateZ = resultSet.getDouble("coordinate_z");

                playerHome = new PlayerHome(homeName, Bukkit.getWorld(world), coordinateX, coordinateY, coordinateZ);
            }
        } catch (SQLException error) {
            logger.severe(String.format(
                    "Unable to get player home by player uuid and name: %s", error.getMessage()
            ));
        } finally {
            database.disconnect();
        }

        return playerHome;
    }

    public static List<PlayerHome> getPlayerHomeList(UUID playerUuid) {
        Database database = new Database();
        database.connect();

        List<PlayerHome> playerHomeList = new ArrayList<>();

        try {
            String query = """
                        SELECT
                            *
                        FROM
                            player_home
                        WHERE
                            player_id = (SELECT id FROM player_profile WHERE uuid = ?);
                    """;

            HashMap<Integer, String> replacements = new HashMap<>();
            replacements.put(1, String.valueOf(playerUuid));
            ResultSet resultSet = database.queryWithWhereClause(query, replacements);

            while (resultSet.next()) {
                String homeName = resultSet.getString("name");
                String world = resultSet.getString("world_type");
                double coordinateX = resultSet.getDouble("coordinate_x");
                double coordinateY = resultSet.getDouble("coordinate_y");
                double coordinateZ = resultSet.getDouble("coordinate_z");

                PlayerHome playerHome = new PlayerHome(homeName, Bukkit.getWorld(world),
                        coordinateX, coordinateY, coordinateZ);
                playerHomeList.add(playerHome);
            }
        } catch (SQLException error) {
            logger.severe(String.format("Unable to get player home: %s", error.getMessage()));
        } finally {
            database.disconnect();
        }

        return playerHomeList;
    }

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
