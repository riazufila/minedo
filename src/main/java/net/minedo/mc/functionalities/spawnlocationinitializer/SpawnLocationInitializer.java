package net.minedo.mc.functionalities.spawnlocationinitializer;

import net.minedo.mc.constants.spawnlocation.SpawnLocation;
import net.minedo.mc.constants.worldtype.WorldType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.logging.Logger;

public class SpawnLocationInitializer {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public void setSpawnLocation() {
        this.logger.info("Setting spawn location.");
        World world = Bukkit.getWorld(WorldType.WORLD.getType());

        try {
            if (world == null) {
                throw new Exception("World is not found.");
            }

            world.setSpawnLocation(
                    SpawnLocation.POSITION_X.getPosition(),
                    // Any highest block at Y that isn't air block.
                    world.getHighestBlockYAt(
                            SpawnLocation.POSITION_X.getPosition(),
                            SpawnLocation.POSITION_Z.getPosition()
                    ) + 1,
                    SpawnLocation.POSITION_Z.getPosition()
            );
        } catch (Exception exception) {
            this.logger.severe(String.format("Unable to set spawn location: %s", exception.getMessage()));
        }
    }

    public boolean hasSpawnLocationSet() {
        World world = Bukkit.getWorld(WorldType.WORLD.getType());

        if (world == null) {
            return false;
        }

        Location spawnlocation = world.getSpawnLocation();

        return spawnlocation.getBlockX() == SpawnLocation.POSITION_X.getPosition()
                && spawnlocation.getBlockZ() == SpawnLocation.POSITION_Z.getPosition();
    }

}
