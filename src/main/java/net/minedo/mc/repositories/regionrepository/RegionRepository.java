package net.minedo.mc.repositories.regionrepository;

import net.minedo.mc.models.region.Region;
import net.minedo.mc.repositories.Database;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Region repository.
 */
public final class RegionRepository {

    private static final Logger logger = Logger.getLogger(RegionRepository.class.getName());

    /**
     * Get all regions.
     *
     * @return all regions.
     */
    public static @Nullable List<Region> getAllRegions() {
        Database database = new Database();
        database.connect();

        List<Region> regions = null;

        try {
            String query = """
                        SELECT name, world_type, min_x, max_x, min_z, max_z, destruction_threshold FROM region;
                    """;

            try (ResultSet resultSet = database.query(query)) {
                while (resultSet.next()) {
                    if (regions == null) {
                        regions = new ArrayList<>();
                    }

                    String name = resultSet.getString("name");
                    String worldType = resultSet.getString("world_type");
                    int minX = resultSet.getInt("min_x");
                    int maxX = resultSet.getInt("max_x");
                    int minZ = resultSet.getInt("min_z");
                    int maxZ = resultSet.getInt("max_z");
                    double destructionThreshold = resultSet.getDouble("destruction_threshold");

                    World world = Bukkit.getWorld(worldType);

                    if (world == null) {
                        throw new RuntimeException("World is invalid");
                    }

                    Region region = new Region(name, world, minX, maxX, minZ, maxZ, destructionThreshold);
                    regions.add(region);
                }
            }
        } catch (SQLException error) {
            logger.severe(String.format("Unable to get regions: %s", error.getMessage()));
        } finally {
            database.disconnect();
        }

        return regions;
    }

}
