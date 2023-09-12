package net.minedo.mc.spawnlocationinitializer;

import net.minedo.mc.constants.spawnlocation.SpawnLocation;
import org.bukkit.Location;
import org.bukkit.World;

public class SpawnLocationInitializer {
    private final World world;
    public SpawnLocationInitializer(World world) {
        this.world = world;

        if (!hasSpawnLocationSet()) {
            setSpawnLocation();
        }
    }

    public void setSpawnLocation() {
        this.world.setSpawnLocation(
                SpawnLocation.POSITION_X.getPosition(),
                // Any highest block at Y that isn't air block.
                this.world.getHighestBlockYAt(
                        SpawnLocation.POSITION_X.getPosition(),
                        SpawnLocation.POSITION_Z.getPosition()
                ),
                SpawnLocation.POSITION_Z.getPosition()
        );
    }

    public boolean hasSpawnLocationSet() {
        Location spawnlocation = this.world.getSpawnLocation();

        return spawnlocation.getBlockX() == SpawnLocation.POSITION_X.getPosition()
                && spawnlocation.getBlockZ() == SpawnLocation.POSITION_Z.getPosition();
    }
}
