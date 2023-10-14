package net.minedo.mc.constants.command.message.regionteleportmessage;

import net.minedo.mc.constants.command.type.customcommandtype.CustomCommandType;
import org.jetbrains.annotations.NotNull;

/**
 * Region teleport command texts.
 */
public enum RegionTeleportMessage {

    SUCCESS_TELEPORT("Teleported to %s."),
    INFO_TELEPORTING("Teleporting to %s in 5.."),
    INFO_COUNTDOWN("%s.."),
    ERROR_USAGE(String.format("Usage: /%s <region>", CustomCommandType.REGION_TELEPORT.getType())),
    ERROR_TELEPORTATION_CANCELLED("Teleportation cancelled.");

    private final String message;

    /**
     * Region teleport command text.
     *
     * @param message text
     */
    RegionTeleportMessage(@NotNull String message) {
        this.message = message;
    }

    /**
     * Get text.
     *
     * @return text
     */
    public @NotNull String getMessage() {
        return message;
    }

}
