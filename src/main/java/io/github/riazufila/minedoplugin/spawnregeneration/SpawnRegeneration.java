package io.github.riazufila.minedoplugin.spawnregeneration;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;

public class SpawnRegeneration implements Listener {

    private World world;
    private WorldGuard worldGuard;


    public SpawnRegeneration(World world, WorldGuard worldGuard) {
        this.world = world;
        this.worldGuard = worldGuard;

        // Initialize spawn region setup.
        if (this.getSpawnRegion() == null) {
            this.setSpawnRegion();
        }
    }

    private ProtectedRegion getSpawnRegion() {
        RegionContainer regionContainer = worldGuard.getPlatform().getRegionContainer();
        RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(this.world));
        ProtectedRegion protectedRegion = regionManager.getRegion("spawn");

        return protectedRegion;
    }

    private void setSpawnRegion() {
        BlockVector3 min = BlockVector3.at(-128, this.world.getMinHeight(), -128);
        BlockVector3 max = BlockVector3.at(128, this.world.getMaxHeight(), 128);
        ProtectedRegion protectedRegion = new ProtectedCuboidRegion("spawn", min, max);
        RegionContainer regionContainer = worldGuard.getPlatform().getRegionContainer();

        regionContainer.get(BukkitAdapter.adapt(this.world)).addRegion(protectedRegion);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        // TODO: Check if block is in 'spawn' region.
    }
}
