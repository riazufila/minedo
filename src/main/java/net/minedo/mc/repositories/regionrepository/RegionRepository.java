package net.minedo.mc.repositories.regionrepository;

import net.minedo.mc.models.region.Region;
import net.minedo.mc.repositories.Database;
import org.bukkit.Bukkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class RegionRepository {

    private static final Logger logger = Logger.getLogger(RegionRepository.class.getName());

    public static List<Region> getAllRegions() {
        Database database = new Database();
        database.connect();

        List<Region> regions = new ArrayList<>();

        try {
            String query = """
                        SELECT name, world_type, minX, maxX, minZ, maxZ FROM region;
                    """;

            try (ResultSet resultSet = database.query(query)) {
                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String world = resultSet.getString("world_type");
                    int minX = resultSet.getInt("minX");
                    int maxX = resultSet.getInt("maxX");
                    int minZ = resultSet.getInt("minZ");
                    int maxZ = resultSet.getInt("maxZ");

                    Region region = new Region(name, Bukkit.getWorld(world), minX, maxX, minZ, maxZ);
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
