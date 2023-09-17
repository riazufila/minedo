package net.minedo.mc.constants.filetype;

public enum FileType {

    SCHEMATIC("schem");

    private final String type;

    FileType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
