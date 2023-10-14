package net.minedo.mc.constants.directory;

import org.jetbrains.annotations.NotNull;

/**
 * Directories.
 */
public enum Directory {

    SCHEMATIC("plugins/WorldEdit/schematics/");

    private final String path;

    /**
     * Directories.
     *
     * @param path directory path
     */
    Directory(@NotNull String path) {
        this.path = path;
    }

    /**
     * Get directory path.
     *
     * @return directory path
     */
    public @NotNull String getDirectory() {
        return path;
    }

}
