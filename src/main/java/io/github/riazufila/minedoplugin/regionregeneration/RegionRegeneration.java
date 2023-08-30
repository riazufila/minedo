package io.github.riazufila.minedoplugin.regionregeneration;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import io.github.riazufila.minedoplugin.database.model.region.Region;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class RegionRegeneration implements Listener {

    private World world;
    private WorldGuard worldGuard;
    private Region region;


    public RegionRegeneration(World world, WorldGuard worldGuard, Region region) {
        this.world = world;
        this.worldGuard = worldGuard;
        this.region = region;

        // Initialize spawn region setup.
        if (this.getRegion() == null) {
            this.setRegion();
        }
    }

    private ProtectedRegion getRegion() {
        RegionContainer regionContainer = worldGuard.getPlatform().getRegionContainer();
        RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(this.world));
        ProtectedRegion protectedRegion = regionManager.getRegion(region.getName());

        return protectedRegion;
    }

    private void setRegion() {
        BlockVector3 min = BlockVector3.at(
                region.getMinX(),
                region.getMinY() == null ? this.world.getMinHeight() : region.getMinY(),
                region.getMinZ()
        );

        BlockVector3 max = BlockVector3.at(
                region.getMaxX(),
                region.getMaxY() == null ? this.world.getMaxHeight() : region.getMaxY(),
                region.getMaxZ()
        );

        ProtectedRegion protectedRegion = new ProtectedCuboidRegion(region.getName(), min, max);
        RegionContainer regionContainer = worldGuard.getPlatform().getRegionContainer();

        regionContainer.get(BukkitAdapter.adapt(this.world)).addRegion(protectedRegion);
    }

    private void getRegionSnapshot() {
        // TODO: Get region snapshot.
    }

    private void setRegionSnapshot() {
        // TODO: Set region snapshot.
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        // TODO: Check if block is in 'spawn' region.
    }
}
