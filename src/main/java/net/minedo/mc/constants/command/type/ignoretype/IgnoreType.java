package net.minedo.mc.constants.command.type.ignoretype;

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
    IgnoreType(String type) {
        this.type = type;
    }

    /**
     * Get command argument.
     *
     * @return argument
     */
    public String getType() {
        return type;
    }

}
