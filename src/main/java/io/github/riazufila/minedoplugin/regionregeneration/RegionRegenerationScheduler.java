package io.github.riazufila.minedoplugin.regionregeneration;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.session.ClipboardHolder;
import io.github.riazufila.minedoplugin.constants.directory.Directory;
import io.github.riazufila.minedoplugin.constants.filetype.FileType;
import io.github.riazufila.minedoplugin.database.model.region.Region;
import org.bukkit.Chunk;
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

public class RegionRegenerationScheduler extends BukkitRunnable {

    private Chunk chunk;
    private final Region region;
    private final World world;
    private final WorldEdit worldEdit;
    private final Logger logger;
    private final Map<String, Integer> restoringChunks;

    public RegionRegenerationScheduler(
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

    public Map<String, Integer> getRestoringChunks() {
        return this.restoringChunks;
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
        } finally {
            this.restoringChunks.remove(String.format("(%d,%d)", chunk.getX(), chunk.getZ()));
            this.cancel();
        }
    }
}
