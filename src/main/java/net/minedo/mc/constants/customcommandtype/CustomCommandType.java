package net.minedo.mc.constants.customcommandtype;

public enum CustomCommandType {

    PLAYER_TELEPORT("teleport");

    private final String type;

    CustomCommandType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return type;
    }

}
