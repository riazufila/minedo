package net.minedo.mc.regionregeneration;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.minedo.mc.Minedo;
import net.minedo.mc.constants.common.Common;
import net.minedo.mc.constants.directory.Directory;
import net.minedo.mc.constants.filetype.FileType;
import net.minedo.mc.database.model.region.Region;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class RegionRegeneration implements Listener {

    private final World world;
    private final WorldGuard worldGuard;
    private final WorldEdit worldEdit;
    private final Region region;
    private final Minedo pluginInstance;
    private final Logger logger;
    private final Map<String, Integer> restoringChunks = new HashMap<>();

    public RegionRegeneration(
            World world, WorldGuard worldGuard, WorldEdit worldEdit,
            Region region, Minedo pluginInstance, Logger logger
    ) {
        this.world = world;
        this.worldGuard = worldGuard;
        this.worldEdit = worldEdit;
        this.region = region;
        this.pluginInstance = pluginInstance;
        this.logger = logger;

        // Initialize spawn region setup.
        if (this.getRegion() == null) {
            this.setRegion();
            this.setRegionSnapshot();
        }

        if (!this.getRegionSnapshot()) {
            this.setRegionSnapshot();
        }

    }

    private File getFile(int chunkX, int chunkZ) {
        String chunkCoordinate = String.format("%d,%d", chunkX, chunkZ);

        String fileName = String.format(
                "%s-region-(%s).%s",
                this.region.getName().toLowerCase(),
                chunkCoordinate,
                FileType.SCHEMATIC.getType()
        );

        return new File(Directory.SCHEMATIC.getDirectory() + fileName);
    }

    private ProtectedRegion getRegion() {
        this.logger.info(String.format("Getting %s region.", this.region.getName()));

        RegionContainer regionContainer = worldGuard.getPlatform().getRegionContainer();
        RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(this.world));
        ProtectedRegion protectedRegion = Objects.requireNonNull(regionManager).getRegion(region.getName());

        if (protectedRegion == null) {
            this.logger.severe(String.format("Unable to get %s region.", this.region.getName()));
            return null;
        }

        return protectedRegion;
    }

    private void setRegion() {
        this.logger.info(String.format("Setting %s region.", this.region.getName()));

        ProtectedRegion protectedRegion = getProtectedRegion();

        // Set permissions.
        protectedRegion.setFlag(Flags.BUILD, StateFlag.State.ALLOW);
        protectedRegion.setFlag(Flags.BLOCK_BREAK, StateFlag.State.ALLOW);

        RegionContainer regionContainer = worldGuard.getPlatform().getRegionContainer();

        Objects.requireNonNull(regionContainer.get(BukkitAdapter.adapt(this.world))).addRegion(protectedRegion);
    }

    @NotNull
    private ProtectedRegion getProtectedRegion() {
        BlockVector3 min = BlockVector3.at(
                region.getMinX(),
                this.world.getMinHeight(),
                region.getMinZ()
        );

        BlockVector3 max = BlockVector3.at(
                region.getMaxX(),
                this.world.getMaxHeight(),
                region.getMaxZ()
        );

        return new ProtectedCuboidRegion(region.getName(), min, max);
    }

    private boolean getRegionSnapshot() {
        this.logger.info(String.format("Getting %s region snapshot.", this.region.getName()));

        RegionContainer container = worldGuard.getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(this.world));
        ProtectedRegion protectedRegion = Objects.requireNonNull(regionManager).getRegion(this.region.getName());

        int chunkSize = Common.CHUNK_SIZE.getValue();
        int minX = Objects.requireNonNull(protectedRegion).getMinimumPoint().getBlockX();
        int maxX = protectedRegion.getMaximumPoint().getBlockX();
        int minZ = protectedRegion.getMinimumPoint().getBlockZ();
        int maxZ = protectedRegion.getMaximumPoint().getBlockZ();

        // Iterate through chunks and check if a schematic for each chunk has been created.
        for (int chunkX = minX / chunkSize; chunkX <= maxX / chunkSize; chunkX++) {
            for (int chunkZ = minZ / chunkSize; chunkZ <= maxZ / chunkSize; chunkZ++) {
                int chunkMinX = chunkX * chunkSize;
                int chunkMaxX = chunkMinX + chunkSize;
                int chunkMinZ = chunkZ * chunkSize;
                int chunkMaxZ = chunkMinZ + chunkSize;

                // Ensure chunk coordinates do not exceed the bounds of the region.
                chunkMinX = Math.max(chunkMinX, minX);
                chunkMaxX = Math.min(chunkMaxX, maxX);
                chunkMinZ = Math.max(chunkMinZ, minZ);
                chunkMaxZ = Math.min(chunkMaxZ, maxZ);

                if (chunkMinX < chunkMaxX && chunkMinZ < chunkMaxZ) {
                    File file = this.getFile(chunkX, chunkZ);

                    if (!file.exists()) {
                        this.logger.severe(String.format("Unable to get %s region snapshot.", this.region.getName()));
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private void setRegionSnapshot() {
        this.logger.info(String.format("Setting %s region snapshot.", this.region.getName()));

        RegionContainer container = worldGuard.getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(this.world));
        ProtectedRegion protectedRegion = Objects.requireNonNull(regionManager).getRegion(this.region.getName());

        int chunkSize = Common.CHUNK_SIZE.getValue();
        int minX = Objects.requireNonNull(protectedRegion).getMinimumPoint().getBlockX();
        int maxX = protectedRegion.getMaximumPoint().getBlockX();
        int minZ = protectedRegion.getMinimumPoint().getBlockZ();
        int maxZ = protectedRegion.getMaximumPoint().getBlockZ();

        // Iterate through chunks and create a schematic for each chunk.
        for (int chunkX = minX / chunkSize; chunkX <= maxX / chunkSize; chunkX++) {
            for (int chunkZ = minZ / chunkSize; chunkZ <= maxZ / chunkSize; chunkZ++) {
                int chunkMinX = chunkX * chunkSize;
                int chunkMaxX = chunkMinX + chunkSize;
                int chunkMinZ = chunkZ * chunkSize;
                int chunkMaxZ = chunkMinZ + chunkSize;

                // Ensure chunk coordinates do not exceed the bounds of the region.
                chunkMinX = Math.max(chunkMinX, minX);
                chunkMaxX = Math.min(chunkMaxX, maxX);
                chunkMinZ = Math.max(chunkMinZ, minZ);
                chunkMaxZ = Math.min(chunkMaxZ, maxZ);

                if (chunkMinX < chunkMaxX && chunkMinZ < chunkMaxZ) {
                    CuboidRegion cuboidRegion = new CuboidRegion(
                            BukkitAdapter.adapt(this.world),
                            BlockVector3.at(chunkMinX, this.world.getMinHeight(), chunkMinZ),
                            BlockVector3.at(chunkMaxX, this.world.getMaxHeight(), chunkMaxZ)
                    );

                    BlockArrayClipboard blockArrayClipboard = new BlockArrayClipboard(cuboidRegion);
                    EditSession editSession = this.worldEdit.newEditSession(BukkitAdapter.adapt(this.world));

                    ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                            editSession,
                            cuboidRegion,
                            blockArrayClipboard,
                            cuboidRegion.getMinimumPoint()
                    );

                    Operations.complete(forwardExtentCopy);

                    File file = this.getFile(chunkX, chunkZ);

                    try (ClipboardWriter clipboardWriter = BuiltInClipboardFormat.FAST.getWriter(
                            new FileOutputStream(file)
                    )) {
                        clipboardWriter.write(blockArrayClipboard);
                    } catch (IOException e) {
                        this.logger.severe(
                                String.format(
                                        "Unable to set %s region snapshot: %s",
                                        this.region.getName(),
                                        e.getMessage()
                                )
                        );
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public void restoreRegionChunk(Chunk chunk) {
        if (restoringChunks.containsKey(String.format("(%d,%d)", chunk.getX(), chunk.getZ()))) {
            return;
        }

        // Run region regeneration scheduler after 30 seconds.
        RegionRegenerationScheduler regionRegenerationScheduler = new RegionRegenerationScheduler(
                chunk, this.region, this.world, this.worldEdit,
                this.logger, this.pluginInstance, this.restoringChunks
        );
        int restoringTaskId = regionRegenerationScheduler
                .runTaskLater(this.pluginInstance, 600)
                .getTaskId();

        // Place task ID and update restoring chunk details.
        restoringChunks.put(String.format("(%d,%d)", chunk.getX(), chunk.getZ()), restoringTaskId);
    }

    private boolean isWithinRegion(Block block) {
        ApplicableRegionSet applicableRegionSet = this.worldGuard
                .getPlatform()
                .getRegionContainer()
                .createQuery()
                .getApplicableRegions(BukkitAdapter.adapt(block.getLocation()));

        return !applicableRegionSet.getRegions().isEmpty();
    }

    private void regenerate(Block block) {
        Chunk chunk = block.getChunk();

        // Check if block destroyed is in region.
        if (!isWithinRegion(block)) {
            return;
        }

        // Restore chunk.
        restoreRegionChunk(chunk);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        this.regenerate(event.getBlock());
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        this.regenerate(event.getLocation().getBlock());
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        this.regenerate(event.getBlock());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Block block = event.getEntity().getLocation().getBlock();

        if (isWithinRegion(block)) {
            event.setKeepInventory(true);
            event.getDrops().clear();
            event.setKeepLevel(true);
        }
    }
}
