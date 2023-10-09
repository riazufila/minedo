package net.minedo.mc.constants.command.type.colortype;

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
    ColorType(String type) {
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
