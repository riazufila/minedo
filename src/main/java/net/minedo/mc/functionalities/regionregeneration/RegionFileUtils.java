package net.minedo.mc.functionalities.regionregeneration;

import net.minedo.mc.constants.directory.Directory;
import net.minedo.mc.constants.filetype.FileType;
import net.minedo.mc.models.region.Region;
import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegionFileUtils {

    /**
     * Get file based on chunk info.
     *
     * @param chunkX chunk x
     * @param chunkZ chunk z
     * @return file
     */
    public static @NotNull File getFile(@NotNull Region region, int chunkX, int chunkZ) {
        String chunkCoordinate = String.format("%d,%d", chunkX, chunkZ);

        String fileName = String.format(
                "%s-region-(%s).%s",
                region.name().toLowerCase(),
                chunkCoordinate,
                FileType.SCHEMATIC.getType()
        );

        return new File(Directory.SCHEMATIC.getDirectory() + fileName);
    }

    /**
     * Get schematic file for region building.
     *
     * @param chunk chunk
     * @return schematic file for region building
     */
    public static @Nullable File getSchematicFileForBuilding(@NotNull Region region, @NotNull Chunk chunk) {
        File schematicFile = null;
        File schematicPath = new File(Directory.SCHEMATIC.getDirectory());
        File[] files = schematicPath.listFiles();

        if (files == null) {
            return null;
        }

        String SPAWN_REGION_SCHEMATIC_REGEX = String.format(
                "%s-region-\\((-?\\d+),(-?\\d+)\\)\\.%s",
                region.name().toLowerCase(),
                FileType.SCHEMATIC.getType()
        );

        Pattern pattern = Pattern.compile(SPAWN_REGION_SCHEMATIC_REGEX);

        for (File file : files) {
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

        return schematicFile;
    }

}
