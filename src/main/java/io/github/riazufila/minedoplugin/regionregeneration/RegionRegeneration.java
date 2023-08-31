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

public class RegionRegeneration implements Listener {

    private final World world;
    private final WorldGuard worldGuard;
    private final WorldEdit worldEdit;
    private final Region region;
    private final File schematicPath;


    public RegionRegeneration(World world, WorldGuard worldGuard, WorldEdit worldEdit, Region region) {
        this.world = world;
        this.worldGuard = worldGuard;
        this.worldEdit = worldEdit;
        this.region = region;
        this.schematicPath = new File(Directory.SCHEMATIC.getDirectory() + String.format(
                "%s-region.%s",
                this.region.getName(),
                FileType.SCHEMATIC.getType()
        ));

        // Initialize spawn region setup.
        if (this.getRegion() == null) {
            this.setRegion();
            this.setRegionSnapshot();
        }

        if (!this.getRegionSnapshot()) {
            this.setRegionSnapshot();
        }

    }

    private ProtectedRegion getRegion() {
        RegionContainer regionContainer = worldGuard.getPlatform().getRegionContainer();
        RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(this.world));

        return Objects.requireNonNull(regionManager).getRegion(region.getName());
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

        Objects.requireNonNull(regionContainer.get(BukkitAdapter.adapt(this.world))).addRegion(protectedRegion);
    }

    private boolean getRegionSnapshot() {
        return this.schematicPath.exists();
    }

    private void setRegionSnapshot() {
        RegionContainer container = worldGuard.getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(this.world));
        ProtectedRegion protectedRegion = Objects.requireNonNull(regionManager).getRegion(this.region.getName());

        CuboidRegion cuboidRegion = new CuboidRegion(
                BukkitAdapter.adapt(this.world),
                Objects.requireNonNull(protectedRegion).getMinimumPoint(),
                protectedRegion.getMaximumPoint()
        );

        BlockArrayClipboard blockArrayClipboard = new BlockArrayClipboard(cuboidRegion);
        EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(this.world));

        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                editSession,
                cuboidRegion,
                blockArrayClipboard,
                cuboidRegion.getMinimumPoint()
        );

        Operations.complete(forwardExtentCopy);

        try (ClipboardWriter clipboardWriter = BuiltInClipboardFormat.FAST.getWriter(
                new FileOutputStream(this.schematicPath)
        )) {
            clipboardWriter.write(blockArrayClipboard);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void restoreRegion() {
        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(this.schematicPath);

        try (ClipboardReader clipboardReader = Objects
                .requireNonNull(clipboardFormat)
                .getReader(
                        new FileInputStream(this.schematicPath)
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

        restoreRegion();
    }
}
