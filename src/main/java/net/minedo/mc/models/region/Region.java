package net.minedo.mc.models.region;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.Random;

public record Region(String name, World worldType, int minX, int maxX, int minZ, int maxZ) {

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
