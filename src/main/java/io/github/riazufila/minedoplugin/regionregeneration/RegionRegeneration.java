package io.github.riazufila.minedoplugin.regionregeneration;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import io.github.riazufila.minedoplugin.constants.directory.Directory;
import io.github.riazufila.minedoplugin.constants.filetype.FileType;
import io.github.riazufila.minedoplugin.database.model.region.Region;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class RegionRegeneration implements Listener {

    private final World world;
    private final WorldGuard worldGuard;
    private final Region region;
    private final File schematicPath;


    public RegionRegeneration(World world, WorldGuard worldGuard, Region region) {
        this.world = world;
        this.worldGuard = worldGuard;
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
        if (this.schematicPath.exists()) {
            return true;
        }

        return false;
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

    public void restoreChunk() {
        // TODO: Restore chunk.
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        // TODO: Check if block is in 'spawn' region.
    }
}
