package net.minedo.mc.constants.command.type.nicknametype;

import org.jetbrains.annotations.NotNull;

/**
 * Nickname command arguments.
 */
public enum NicknameType {

    SET("set"),
    REVEAL("reveal"),
    REMOVE("remove");

    private final String type;

    /**
     * Nickname command argument.
     *
     * @param type command argument.
     */
    NicknameType(@NotNull String type) {
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
