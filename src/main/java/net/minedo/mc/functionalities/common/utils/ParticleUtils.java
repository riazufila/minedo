package net.minedo.mc.functionalities.common.utils;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Particle utils.
 */
public final class ParticleUtils {

    /**
     * Spawn particles on bounding box.
     *
     * @param entity      entity
     * @param particle    particle as in {@link Particle#values()}
     * @param density     particles density
     * @param count       particles count
     * @param dustOptions dust options
     * @param offset      offset
     */
    private static void spawnParticleOnBoundingBox(
            @NotNull Entity entity, @NotNull Particle particle, double density,
            int count, @Nullable Particle.DustOptions dustOptions, @Nullable Double offset
    ) {
        BoundingBox boundingBox = entity.getBoundingBox();
        World world = entity.getWorld();

        // Calculate the step size based on the density
        double stepX = boundingBox.getWidthX() / density;
        double stepY = boundingBox.getHeight() / density;
        double stepZ = boundingBox.getWidthZ() / density;

        // Iterate over the bounding box and spawn particles
        for (double x = boundingBox.getMinX(); x <= boundingBox.getMaxX(); x += stepX) {
            for (double y = boundingBox.getMinY(); y <= boundingBox.getMaxY(); y += stepY) {
                for (double z = boundingBox.getMinZ(); z <= boundingBox.getMaxZ(); z += stepZ) {
                    Location particleLocation = new Location(world, x, y, z);

                    if (offset != null) {
                        world.spawnParticle(
                                particle, particleLocation, count,
                                offset, offset, offset, dustOptions
                        );
                    } else {
                        world.spawnParticle(particle, particleLocation, count, dustOptions);
                    }
                }
            }
        }
    }

    /**
     * Spawn particles on an entity.
     *
     * @param entity      entity
     * @param particle    particle as in {@link Particle#values()}
     * @param density     particles density
     * @param count       particles count
     * @param dustOptions dust options
     */
    public static void spawnParticleOnEntity(
            @NotNull Entity entity, @NotNull Particle particle, double density,
            int count, @Nullable Particle.DustOptions dustOptions
    ) {
        spawnParticleOnBoundingBox(entity, particle, density, count, dustOptions, null);
    }

    /**
     * Spawn particles on an entity.
     *
     * @param entity      entity
     * @param particle    particle as in {@link Particle#values()}
     * @param density     particles density
     * @param count       particles count
     * @param dustOptions dust options
     * @param offset      offset
     */
    public static void spawnParticleOnEntity(
            @NotNull Entity entity, @NotNull Particle particle, double density,
            int count, @NotNull Particle.DustOptions dustOptions, double offset
    ) {
        spawnParticleOnBoundingBox(entity, particle, density, count, dustOptions, offset);
    }

}
