package net.minedo.mc.constants.directory;

public enum Directory {

    SCHEMATIC("plugins/WorldEdit/schematics/");

    private final String path;

    Directory(String path) {
        this.path = path;
    }

    public String getDirectory() {
        return path;
    }

}
