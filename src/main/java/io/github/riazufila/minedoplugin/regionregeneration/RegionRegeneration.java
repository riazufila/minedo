package io.github.riazufila.minedoplugin.regionregeneration;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import io.github.riazufila.minedoplugin.constants.common.Common;
import io.github.riazufila.minedoplugin.constants.directory.Directory;
import io.github.riazufila.minedoplugin.constants.filetype.FileType;
import io.github.riazufila.minedoplugin.database.model.region.Region;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.io.*;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegionRegeneration implements Listener {

    private final World world;
    private final WorldGuard worldGuard;
    private final WorldEdit worldEdit;
    private final Region region;
    private final Logger logger;

    public RegionRegeneration(World world, WorldGuard worldGuard, WorldEdit worldEdit, Region region, Logger logger) {
        this.world = world;
        this.worldGuard = worldGuard;
        this.worldEdit = worldEdit;
        this.region = region;
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
                this.region.getName(),
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

        Objects.requireNonNull(regionContainer.get(BukkitAdapter.adapt(this.world))).addRegion(protectedRegion);
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
        this.logger.info(String.format(
                "Restoring chunk (%d, %d) in %s region.",
                chunk.getX(),
                chunk.getZ(),
                this.region.getName())
        );

        File schematicFile = null;
        File schematicPath = new File(Directory.SCHEMATIC.getDirectory());
        File[] files = schematicPath.listFiles();

        String SPAWN_REGION_SCHEMATIC_REGEX = String.format(
                "%s-region-\\((-?\\d+),(-?\\d+)\\)\\.%s",
                this.region.getName(),
                FileType.SCHEMATIC.getType()
        );

        Pattern pattern = Pattern.compile(SPAWN_REGION_SCHEMATIC_REGEX);

        for (File file : Objects.requireNonNull(files)) {
            Matcher matcher = pattern.matcher(file.getName());

            if (matcher.matches()) {
                int chunkX = Integer.parseInt(matcher.group(1));
                int chunkZ = Integer.parseInt(matcher.group(2));

                if (chunkX == chunk.getX() && chunkZ == chunk.getZ()) {
                    schematicFile = file;
                    break;
                }
            }
        }

        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(Objects.requireNonNull(schematicFile));

        try (ClipboardReader clipboardReader = Objects
                .requireNonNull(clipboardFormat)
                .getReader(
                        new FileInputStream(schematicFile)
                )
        ) {
            Clipboard clipboard = clipboardReader.read();

            try (EditSession editSession = this.worldEdit.newEditSession(BukkitAdapter.adapt(this.world))) {
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(clipboard.getOrigin())
                        .ignoreAirBlocks(true)
                        .copyEntities(false)
                        .build();

                Operations.complete(operation);
            }
        } catch (IOException e) {
            this.logger.severe(String.format(
                    "Unable to restore chunk (%d, %d) in %s region.",
                    chunk.getX(),
                    chunk.getZ(),
                    this.region.getName())
            );
            throw new RuntimeException(e);
        }
    }

    public boolean isChunkDestroyedAboveThreshold(Chunk chunk) {
        // TODO: Calculate destruction level.

        return true;
    }

    private boolean isWithinRegion(Block block) {
        ApplicableRegionSet applicableRegionSet = this.worldGuard
                .getPlatform()
                .getRegionContainer()
                .createQuery()
                .getApplicableRegions(BukkitAdapter.adapt(block.getLocation()));

        return !applicableRegionSet.getRegions().isEmpty();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        // Check if block destroyed is in region.
        if (!isWithinRegion(block)) {
            return;
        }

        // Calculate how much of the chunk is destroyed.
        Chunk chunk = block.getChunk();
        if (!isChunkDestroyedAboveThreshold(chunk)) {
            return;
        }

         restoreRegionChunk(chunk);
    }
}
