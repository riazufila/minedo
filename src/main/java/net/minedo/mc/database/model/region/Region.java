package net.minedo.mc.database.model.region;

import net.minedo.mc.database.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Region {
    public int id;
    public String name;
    public int minX;
    public int maxX;
    public int minZ;
    public int maxZ;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMinX() {
        return minX;
    }

    public void setMinX(int minX) {
        this.minX = minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    public int getMinZ() {
        return minZ;
    }

    public void setMinZ(int minZ) {
        this.minZ = minZ;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public void setMaxZ(int maxZ) {
        this.maxZ = maxZ;
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
                int minX = resultSet.getInt("minX");
                int maxX = resultSet.getInt("maxX");
                int minZ = resultSet.getInt("minZ");
                int maxZ = resultSet.getInt("maxZ");

                // Set Region object and push to Array.
                Region region = new Region();
                region.setId(id);
                region.setName(name);
                region.setMinX(minX);
                region.setMaxX(maxX);
                region.setMinZ(minZ);
                region.setMaxZ(maxZ);

                regions.add(region);
            }
        } catch (SQLException error) {
            error.printStackTrace();
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
                int minX = resultSet.getInt("minX");
                int maxX = resultSet.getInt("maxX");
                int minZ = resultSet.getInt("minZ");
                int maxZ = resultSet.getInt("maxZ");

                // Set Region object return.
                region.setId(id);
                region.setName(name);
                region.setMinX(minX);
                region.setMaxX(maxX);
                region.setMinZ(minZ);
                region.setMaxZ(maxZ);
            }
        } catch (SQLException error) {
            error.printStackTrace();
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
                int minX = resultSet.getInt("minX");
                int maxX = resultSet.getInt("maxX");
                int minZ = resultSet.getInt("minZ");
                int maxZ = resultSet.getInt("maxZ");

                // Set Region object and push to Array.
                region.setId(id);
                region.setName(name);
                region.setMinX(minX);
                region.setMaxX(maxX);
                region.setMinZ(minZ);
                region.setMaxZ(maxZ);
            }
        } catch (SQLException error) {
            error.printStackTrace();
        } finally {
            database.disconnect();
        }

        return region;
    }
}
