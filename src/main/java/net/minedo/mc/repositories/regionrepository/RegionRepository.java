package net.minedo.mc.repositories.regionrepository;

import net.minedo.mc.Minedo;
import net.minedo.mc.models.region.Region;
import net.minedo.mc.repositories.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class RegionRepository {

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final Minedo pluginInstance;

    public RegionRepository(Minedo pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    public List<Region> getAllRegions() {
        Database database = new Database();
        database.connect();

        List<Region> regions = new ArrayList<>();

        try {
            String query = """
                        SELECT * FROM region;
                    """;
            ResultSet resultSet = database.query(query);

            while (resultSet.next()) {
                // Retrieve Region.
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String world = resultSet.getString("worldType");
                int minX = resultSet.getInt("minX");
                int maxX = resultSet.getInt("maxX");
                int minZ = resultSet.getInt("minZ");
                int maxZ = resultSet.getInt("maxZ");

                // Set Region object and push to Array.
                Region region = new Region();
                region.setId(id);
                region.setName(name);
                region.setWorldType(this.pluginInstance.getWorldBasedOnName(world));
                region.setMinX(minX);
                region.setMaxX(maxX);
                region.setMinZ(minZ);
                region.setMaxZ(maxZ);

                regions.add(region);
            }
        } catch (SQLException error) {
            this.logger.severe(String.format("Unable to get regions: %s", error.getMessage()));
        } finally {
            database.disconnect();
        }

        return regions;
    }


    public Region getRegionById(int id) {
        Database database = new Database();
        database.connect();

        Region region = new Region();

        try {
            String query = """
                        SELECT * FROM region WHERE id = ?;
                    """;
            Map<Integer, String> replacements = Collections.singletonMap(1, Integer.toString(id));
            ResultSet resultSet = database.queryWithWhereClause(query, replacements);

            if (resultSet.next()) {
                // Retrieve Region.
                String name = resultSet.getString("name");
                String world = resultSet.getString("worldType");
                int minX = resultSet.getInt("minX");
                int maxX = resultSet.getInt("maxX");
                int minZ = resultSet.getInt("minZ");
                int maxZ = resultSet.getInt("maxZ");

                // Set Region object return.
                region.setId(id);
                region.setName(name);
                region.setWorldType(this.pluginInstance.getWorldBasedOnName(world));
                region.setMinX(minX);
                region.setMaxX(maxX);
                region.setMinZ(minZ);
                region.setMaxZ(maxZ);
            }
        } catch (SQLException error) {
            this.logger.severe(String.format("Unable to get region by id: %s", error.getMessage()));
        } finally {
            database.disconnect();
        }

        return region;
    }

    public Region getRegionByName(String value) {
        Database database = new Database();
        database.connect();

        Region region = new Region();

        try {
            String query = """
                        SELECT * FROM region WHERE name = ?;
                    """;
            Map<Integer, String> replacements = Collections.singletonMap(1, value);
            ResultSet resultSet = database.queryWithWhereClause(query, replacements);

            if (resultSet.next()) {
                // Retrieve Region.
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String world = resultSet.getString("worldType");
                int minX = resultSet.getInt("minX");
                int maxX = resultSet.getInt("maxX");
                int minZ = resultSet.getInt("minZ");
                int maxZ = resultSet.getInt("maxZ");

                // Set Region object and push to Array.
                region.setId(id);
                region.setName(name);
                region.setWorldType(this.pluginInstance.getWorldBasedOnName(world));
                region.setMinX(minX);
                region.setMaxX(maxX);
                region.setMinZ(minZ);
                region.setMaxZ(maxZ);
            }
        } catch (SQLException error) {
            this.logger.severe(String.format("Unable to get region by name: %s", error.getMessage()));
        } finally {
            database.disconnect();
        }

        return region;
    }

}
