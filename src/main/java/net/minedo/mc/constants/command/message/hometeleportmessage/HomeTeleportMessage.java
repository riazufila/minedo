package net.minedo.mc.constants.command.message.hometeleportmessage;

import net.minedo.mc.constants.command.type.customcommandtype.CustomCommandType;
import net.minedo.mc.constants.command.type.hometype.HomeType;

public enum HomeTeleportMessage {

    SUCCESS_TELEPORT("Teleported to %s!"),
    INFO_TELEPORTING("Teleporting to %s in 5.."),
    INFO_COUNTDOWN("%s.."),
    ERROR_USAGE(String.format(
            "/%s <%s | %s | %s> <home>",
            CustomCommandType.HOME.getMessage(),
            HomeType.TELEPORT.getType(),
            HomeType.SET.getType(),
            HomeType.REMOVE.getType()
    )),
    ERROR_TELEPORTATION_CANCELLED("Teleportation cancelled.");

    private final String message;

    HomeTeleportMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
