package net.minedo.mc.constants.command.type.customcommandtype;

public enum CustomCommandType {

    REGION_TELEPORT("warp"),
    PLAYER_TELEPORT("teleport"),
    NARRATE("narrate"),
    LIKE("like"),
    IGNORE("ignore"),
    MESSAGE("message"),
    COLOR("color");

    private final String type;

    CustomCommandType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return type;
    }

}