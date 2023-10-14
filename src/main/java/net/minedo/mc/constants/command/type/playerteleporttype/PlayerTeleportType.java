package net.minedo.mc.constants.command.type.playerteleporttype;

import org.jetbrains.annotations.NotNull;

/**
 * Player teleport command arguments.
 */
public enum PlayerTeleportType {

    REQUEST("request"),
    ACCEPT("accept"),
    DECLINE("decline"),
    DISCARD("discard");

    private final String type;

    /**
     * Player teleport command argument.
     *
     * @param type command argument.
     */
    PlayerTeleportType(@NotNull String type) {
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
