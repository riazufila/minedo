package net.minedo.mc.constants.command.type.nicknametype;

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
    NicknameType(String type) {
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
