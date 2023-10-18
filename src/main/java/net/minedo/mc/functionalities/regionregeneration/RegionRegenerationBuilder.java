package net.minedo.mc.functionalities.regionregeneration;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import net.minedo.mc.constants.common.Common;
import net.minedo.mc.constants.feedbacksound.FeedbackSound;
import net.minedo.mc.models.region.Region;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Region builder runnable.
 */
public class RegionRegenerationBuilder extends BukkitRunnable {

    private final Chunk chunk;
    private final Region region;
    private final HashMap<String, Integer> restoringChunks;
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Initialize region builder runnable.
     *
     * @param chunk           chunk
     * @param region          region
     * @param restoringChunks chunks that are restoring
     */
    public RegionRegenerationBuilder(
            @NotNull Chunk chunk, @NotNull Region region, @NotNull HashMap<String, Integer> restoringChunks
    ) {
        this.chunk = chunk;
        this.region = region;
        this.restoringChunks = restoringChunks;
    }

    /**
     * Get center of chunk.
     *
     * @param chunk chunk
     * @return center of chunk
     */
    private @NotNull Location getCenterLocationOfChunk(@NotNull Chunk chunk) {
        World world = this.region.worldType();
        int CHUNK_SIZE = (int) Common.CHUNK_SIZE.getValue();
        int coordinateMinX = chunk.getX() * CHUNK_SIZE;
        int coordinateMinZ = chunk.getZ() * CHUNK_SIZE;
        // Skip subtracting 1 from max coordinates, as we're looking to
        // get the closest center coordinate in int.
        int coordinateMaxX = coordinateMinX + CHUNK_SIZE;
        int coordinateMaxZ = coordinateMinZ + CHUNK_SIZE;
        int centerCoordinateX = (coordinateMinX + coordinateMaxX) / 2;
        int centerCoordinateZ = (coordinateMinZ + coordinateMaxZ) / 2;

        return new Location(
                world,
                centerCoordinateX,
                world.getHighestBlockYAt(centerCoordinateX, centerCoordinateZ),
                centerCoordinateZ
        );
    }

    /**
     * Get the lowest coordinate Y of a given coordinate X and Z.
     *
     * @param world       world
     * @param coordinateX coordinate X
     * @param coordinateZ coordinate Z
     * @return the lowest coordinate Y of a given coordinate X and Z
     */
    private int getLowestBlockYAt(World world, int coordinateX, int coordinateZ) {
        int minimumWorldHeight = world.getMinHeight();
        int highestBlock = world.getHighestBlockYAt(coordinateX, coordinateZ);
        int lowestBlock = minimumWorldHeight;

        for (int coordinateY = minimumWorldHeight; coordinateY <= highestBlock; coordinateY++) {
            Block block = world.getBlockAt(coordinateX, coordinateY, coordinateZ);

            if (block.getType() != Material.AIR) {
                lowestBlock = coordinateY;
                break;
            }
        }

        return lowestBlock;
    }

    /**
     * Play sound at center of chunk.
     *
     * @param chunk chunk
     */
    private void playSoundAtCenterOfChunk(@NotNull Chunk chunk) {
        Location location = this.getCenterLocationOfChunk(chunk);
        FeedbackSound feedbackSound = FeedbackSound.REGION_REGENERATING;

        this.region.worldType().playSound(location, feedbackSound.getSound(),
                feedbackSound.getVolume(), feedbackSound.getPitch());
    }

    @Override
    public void run() {
        this.logger.info(String.format(
                "Restoring chunk (%d, %d) in %s region.",
                this.chunk.getX(),
                this.chunk.getZ(),
                this.region.name())
        );

        File schematicFile = RegionFileUtils.getSchematicFileForBuilding(this.region, this.chunk);

        if (schematicFile == null) {
            return;
        }

        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(schematicFile);

        try (ClipboardReader clipboardReader = Objects
                .requireNonNull(clipboardFormat)
                .getReader(
                        new FileInputStream(schematicFile)
                )
        ) {
            Clipboard clipboard = clipboardReader.read();
            WorldEdit worldEdit = WorldEdit.getInstance();
            World world = this.region.worldType();

            try (EditSession editSession = worldEdit.newEditSession(BukkitAdapter.adapt(world))) {
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(clipboard.getOrigin())
                        .build();

                Operations.complete(operation);
                this.playSoundAtCenterOfChunk(this.chunk);
            } catch (WorldEditException e) {
                throw new RuntimeException(e);
            }

            BlockVector3 minimumPoint = clipboard.getMinimumPoint();
            BlockVector3 maximumPoint = clipboard.getMaximumPoint();
            int minCoordinateX = minimumPoint.getX();
            int maxCoordinateX = maximumPoint.getX();
            int minCoordinateZ = minimumPoint.getZ();
            int maxCoordinateZ = maximumPoint.getZ();

            // Add particles to the outlines of the chunk that is exposed.
            for (int x = minCoordinateX; x <= maxCoordinateX; x++) {
                for (int z = minCoordinateZ; z <= maxCoordinateZ; z++) {
                    for (int y = minimumPoint.getY(); y <= maximumPoint.getY(); y++) {
                        int highestBlock = world.getHighestBlockYAt(x, z);
                        int lowestBlock = this.getLowestBlockYAt(world, x, z);
                        boolean isOutline = ((x == minCoordinateX
                                || x == maxCoordinateX
                                || z == minCoordinateZ
                                || z == maxCoordinateZ)
                                && (y >= lowestBlock && y <= highestBlock))
                                || y == lowestBlock
                                || y == highestBlock;

                        if (isOutline) {
                            world.spawnParticle(
                                    Particle.COMPOSTER,
                                    x,
                                    y,
                                    z,
                                    1,
                                    1.5,
                                    1.5,
                                    1.5,
                                    0
                            );
                        }
                    }
                }
            }
        } catch (IOException e) {
            this.logger.severe(String.format(
                    "Unable to restore chunk (%d, %d) in %s region.",
                    chunk.getX(),
                    chunk.getZ(),
                    this.region.name())
            );
            throw new RuntimeException(e);
        } finally {
            this.restoringChunks.remove(String.format("(%d,%d)", chunk.getX(), chunk.getZ()));
        }
    }

}
