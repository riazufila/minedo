package net.minedo.mc.regionregeneration;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import net.minedo.mc.constants.directory.Directory;
import net.minedo.mc.constants.filetype.FileType;
import net.minedo.mc.database.model.region.Region;
import org.bukkit.Chunk;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegionRegenerationBuilder extends BukkitRunnable {
    private final Chunk chunk;
    private final Region region;
    private final World world;
    private final WorldEdit worldEdit;
    private final Logger logger;
    private final Map<String, Integer> restoringChunks;

    public RegionRegenerationBuilder(
            Chunk chunk, Region region, World world,
            WorldEdit worldEdit, Logger logger, Map<String, Integer> restoringChunks
    ) {
        this.chunk = chunk;
        this.region = region;
        this.world = world;
        this.worldEdit = worldEdit;
        this.logger = logger;
        this.restoringChunks = restoringChunks;
    }

    @Override
    public void run() {
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
                this.region.getName().toLowerCase(),
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

            BlockVector3 minimumPoint = clipboard.getMinimumPoint();
            BlockVector3 maximumPoint = clipboard.getMaximumPoint();

            // Add particles to the top part of the chunk.
            for (int x = minimumPoint.getX(); x <= maximumPoint.getX(); x++) {
                for (int z = minimumPoint.getZ(); z <= maximumPoint.getZ(); z++) {
                    int y = this.world.getHighestBlockYAt(x, z);
                    // Spawn the particle effect
                    this.world.spawnParticle(
                            Particle.COMPOSTER,
                            x,
                            y,
                            z,
                            10,
                            1,
                            1,
                            1
                    );
                }
            }
        } catch (IOException e) {
            this.logger.severe(String.format(
                    "Unable to restore chunk (%d, %d) in %s region.",
                    chunk.getX(),
                    chunk.getZ(),
                    this.region.getName())
            );
            throw new RuntimeException(e);
        } finally {
            this.restoringChunks.remove(String.format("(%d,%d)", chunk.getX(), chunk.getZ()));
        }
    }
}
