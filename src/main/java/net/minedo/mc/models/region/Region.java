package net.minedo.mc.models.region;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.Random;

public class Region {

    private final int id;
    private final String name;
    private final World worldType;
    private final int minX;
    private final int maxX;
    private final int minZ;
    private final int maxZ;

    public Region(int id, String name, World worldType, int minX, int maxX, int minZ, int maxZ) {
        this.id = id;
        this.name = name;
        this.worldType = worldType;
        this.minX = minX;
        this.maxX = maxX;
        this.minZ = minZ;
        this.maxZ = maxZ;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public World getWorldType() {
        return worldType;
    }

    public int getMinX() {
        return minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMinZ() {
        return minZ;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public Location getCenter() {
        // Add 1 to max coordinate to conform with chunk size.
        int centerCoordinateX = (this.minX + (this.maxX + 1)) / 2;
        int centerCoordinateZ = (this.minZ + (this.maxZ + 1)) / 2;

        return new Location(
                worldType,
                centerCoordinateX,
                worldType.getHighestBlockYAt(centerCoordinateX, centerCoordinateZ),
                centerCoordinateZ
        );
    }

    public Location getRandomLocation() {
        Random random = new Random();

        int coordinateX = random.nextInt((this.maxX - this.minX + 1) + this.minX);
        int coordinateZ = random.nextInt((this.maxZ - this.minZ + 1) + this.minZ);
        int coordinateY = worldType.getHighestBlockYAt(coordinateX, coordinateZ);

        return new Location(worldType, coordinateX, coordinateY, coordinateZ);
    }

}
