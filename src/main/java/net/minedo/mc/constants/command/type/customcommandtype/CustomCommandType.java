package net.minedo.mc.constants.command.type.customcommandtype;

/**
 * Custom command labels.
 */
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

    /**
     * Custom command label.
     *
     * @param type command label
     */
    CustomCommandType(String type) {
        this.type = type;
    }

    /**
     * Get custom command label.
     *
     * @return command label
     */
    public String getType() {
        return type;
    }

}
