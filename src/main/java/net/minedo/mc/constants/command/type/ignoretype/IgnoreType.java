package net.minedo.mc.constants.command.type.ignoretype;

import org.jetbrains.annotations.NotNull;

/**
 * Ignore command arguments.
 */
public enum IgnoreType {

    ADD("add"),
    REMOVE("remove");

    private final String type;

    /**
     * Ignore command argument.
     *
     * @param type argument
     */
    IgnoreType(@NotNull String type) {
        this.type = type;
    }

    /**
     * Get command argument.
     *
     * @return argument
     */
    public @NotNull String getType() {
        return type;
    }

}
