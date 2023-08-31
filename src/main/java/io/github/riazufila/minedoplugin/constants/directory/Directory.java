package io.github.riazufila.minedoplugin.constants.directory;

public enum Directory {
    SCHEMATIC("plugins/FastAsyncWorldEdit/schematics/");

    private final String path;

    Directory(String path) {
        this.path = path;
    }

    public String getDirectory() {
        return path;
    }
}
