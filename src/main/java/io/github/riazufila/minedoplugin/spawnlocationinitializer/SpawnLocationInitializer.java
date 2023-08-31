package io.github.riazufila.minedoplugin.spawnlocationinitializer;

import org.bukkit.Location;
import org.bukkit.World;
import io.github.riazufila.minedoplugin.constants.spawnlocation.SpawnLocation;

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
                SpawnLocation.positionX.getPosition(),
                // Any highest block at Y that isn't air block.
                this.world.getHighestBlockYAt(
                        SpawnLocation.positionX.getPosition(),
                        SpawnLocation.positionZ.getPosition()
                ),
                SpawnLocation.positionZ.getPosition()
        );
    }

    public boolean hasSpawnLocationSet() {
        Location spawnlocation = this.world.getSpawnLocation();

        return spawnlocation.getBlockX() == SpawnLocation.positionX.getPosition()
                && spawnlocation.getBlockZ() == SpawnLocation.positionZ.getPosition();
    }
}
