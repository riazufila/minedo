package net.minedo.mc.constants.filetype;

/**
 * File type.
 */
public enum FileType {

    SCHEMATIC("schem");

    private final String type;

    /**
     * File type.
     *
     * @param type type
     */
    FileType(String type) {
        this.type = type;
    }

    /**
     * Get file type.
     *
     * @return file type
     */
    public String getType() {
        return type;
    }

}
