package net.minedo.mc.constants.command.message.regionteleportmessage;

import net.minedo.mc.constants.command.type.customcommandtype.CustomCommandType;

public enum RegionTeleportMessage {

    SUCCESS_TELEPORT("Teleported to %s!"),
    INFO_TELEPORTING("Teleporting to %s in 5.."),
    INFO_COUNTDOWN("%s.."),
    ERROR_USAGE(String.format("Usage: /%s <region>", CustomCommandType.REGION_TELEPORT.getMessage())),
    ERROR_TELEPORTATION_CANCELLED("Teleportation cancelled.");

    private final String message;

    RegionTeleportMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
