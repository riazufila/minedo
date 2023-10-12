package net.minedo.mc.functionalities.utils;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Shape utils.
 */
public final class ShapeUtils {

    /**
     * Get locations for a sphere.
     *
     * @param centerLocation center location
     * @param radius         radius
     * @return locations for a sphere
     */
    public static List<Location> getSphere(Location centerLocation, double radius) {
        List<Location> locations = new ArrayList<>();
        double coordinateX = centerLocation.getX();
        double coordinateY = centerLocation.getY();
        double coordinateZ = centerLocation.getZ();
        World world = centerLocation.getWorld();

        for (double a = 0; a < 2 * Math.PI; a += 0.1) {
            for (double b = 0; b < Math.PI; b += 0.1) {
                double x = coordinateX + radius * Math.cos(a) * Math.sin(b);
                double y = coordinateY + radius * Math.sin(a) * Math.sin(b);
                double z = coordinateZ + radius * Math.cos(b);

                Location location = new Location(world, x, y, z);
                locations.add(location);
            }
        }

        return locations;
    }

}
