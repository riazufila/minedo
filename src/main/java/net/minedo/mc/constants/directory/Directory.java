package net.minedo.mc.constants.directory;

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
    Directory(String path) {
        this.path = path;
    }

    /**
     * Get directory path.
     *
     * @return directory path
     */
    public String getDirectory() {
        return path;
    }

}
