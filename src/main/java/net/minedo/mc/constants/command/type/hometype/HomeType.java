package net.minedo.mc.constants.command.type.hometype;

/**
 * Home command arguments.
 */
public enum HomeType {

    TELEPORT("teleport"),
    ADD("add"),
    UPDATE("update"),
    REMOVE("remove");

    private final String type;

    /**
     * Home command argument.
     *
     * @param type command argument
     */
    HomeType(String type) {
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
