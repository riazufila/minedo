package net.minedo.mc.constants.command.type.customcommandtype;

import org.jetbrains.annotations.NotNull;

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
    CustomCommandType(@NotNull String type) {
        this.type = type;
    }

    /**
     * Get custom command label.
     *
     * @return command label
     */
    public @NotNull String getType() {
        return type;
    }

}
