package net.minedo.mc.constants.command.type.colortype;

import org.jetbrains.annotations.NotNull;

/**
 * Color command arguments.
 */
public enum ColorType {

    NAME("name"),
    CHAT("chat"),
    PRESET("preset"),
    CUSTOM("custom"),
    REMOVE("remove");

    private final String type;

    /**
     * Color command argument.
     *
     * @param type command argument
     */
    ColorType(@NotNull String type) {
        this.type = type;
    }

    /**
     * Get command argument.
     *
     * @return command argument
     */
    public @NotNull String getType() {
        return type;
    }

}
