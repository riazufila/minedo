package net.minedo.mc.constants.command.type.playerteleporttype;

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
    PlayerTeleportType(String type) {
        this.type = type;
    }

    /**
     * Get command argument.
     *
     * @return command argument
     */
    public String getType() {
        return type;
    }

}
