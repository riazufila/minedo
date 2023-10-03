package net.minedo.mc.constants.command.type.customcommandtype;

public enum CustomCommandType {

    REGION_TELEPORT("warp"),
    PLAYER_TELEPORT("teleport"),
    HOME_TELEPORT("home"),
    NARRATE("narrate"),
    LIKE("like"),
    IGNORE("ignore"),
    MESSAGE("message"),
    COLOR("color"),
    NICKNAME("nickname");

    private final String type;

    CustomCommandType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return type;
    }

}
