package net.minedo.mc.constants.filetype;

import org.jetbrains.annotations.NotNull;

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
    FileType(@NotNull String type) {
        this.type = type;
    }

    /**
     * Get file type.
     *
     * @return file type
     */
    public @NotNull String getType() {
        return type;
    }

}
