package net.minedo.mc.spawnlocationinitializer;

import net.minedo.mc.Minedo;
import net.minedo.mc.constants.spawnlocation.SpawnLocation;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.logging.Logger;

public class SpawnLocationInitializer {

    private final Minedo pluginInstance;
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public SpawnLocationInitializer(Minedo pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    public void setSpawnLocation() {
        this.logger.info("Setting spawn location.");
        World world = this.pluginInstance.getOverworld();

        try {
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
        World world = this.pluginInstance.getOverworld();
        Location spawnlocation = world.getSpawnLocation();

        return spawnlocation.getBlockX() == SpawnLocation.POSITION_X.getPosition()
                && spawnlocation.getBlockZ() == SpawnLocation.POSITION_Z.getPosition();
    }

}
