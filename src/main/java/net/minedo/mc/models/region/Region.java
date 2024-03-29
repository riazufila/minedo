package net.minedo.mc.models.region;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * Region regenerates upon block is broken after a set amount of time.
 *
 * @param name              name of region
 * @param worldType         world where the region is in
 * @param minX              minimum x coordinate
 * @param maxX              maximum x coordinate
 * @param minZ              minimum z coordinate
 * @param maxZ              maximum z coordinate
 * @param modifiedThreshold threshold until region is regenerated
 */
public record Region(@NotNull String name,
                     @NotNull World worldType,
                     int minX,
                     int maxX,
                     int minZ,
                     int maxZ,
                     double modifiedThreshold) {

    /**
     * Get the center of the region.
     *
     * @return center of region
     */
    public @NotNull Location getCenter() {
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

    /**
     * Gets random location in region.
     *
     * @return random location in region
     */
    public @NotNull Location getRandomLocation() {
        Random random = new Random();

        int coordinateX = random.nextInt((this.maxX - this.minX + 1) + this.minX);
        int coordinateZ = random.nextInt((this.maxZ - this.minZ + 1) + this.minZ);
        int coordinateY = worldType.getHighestBlockYAt(coordinateX, coordinateZ);

        return new Location(worldType, coordinateX, coordinateY, coordinateZ);
    }

    /**
     * Get whether location is within region.
     *
     * @param location location
     * @return whether location is within region
     */
    public boolean isWithinRegion(Location location) {
        World world = this.worldType();

        if (world != location.getWorld()) {
            return false;
        }

        int locationBlockX = location.getBlockX();
        int locationBlockZ = location.getBlockZ();

        return locationBlockX >= this.minX()
                && locationBlockX <= this.maxX()
                && locationBlockZ >= this.minZ()
                && locationBlockZ <= this.maxZ();
    }

}
