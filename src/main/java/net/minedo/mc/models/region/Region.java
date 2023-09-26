package net.minedo.mc.models.region;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.Random;

public class Region {

    private int id;
    private String name;
    private World worldType;
    private int minX;
    private int maxX;
    private int minZ;
    private int maxZ;

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

    public World getWorldType() {
        return worldType;
    }

    public void setWorldType(World worldType) {
        this.worldType = worldType;
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
