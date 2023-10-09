package net.minedo.mc.constants.command.message.hometeleportmessage;

import net.minedo.mc.constants.command.type.customcommandtype.CustomCommandType;
import net.minedo.mc.constants.command.type.hometype.HomeType;

/**
 * Home teleport texts.
 */
public enum HomeTeleportMessage {

    SUCCESS_TELEPORT("Teleported to %s!"),
    SUCCESS_ADD_HOME("Home added."),
    SUCCESS_UPDATE_HOME("Home updated."),
    SUCCESS_REMOVE_HOME("Home removed."),
    INFO_TELEPORTING("Teleporting to %s in 5.."),
    INFO_COUNTDOWN("%s.."),
    ERROR_USAGE(String.format(
            "/%s <%s | %s | %s | %s> <home>",
            CustomCommandType.HOME_TELEPORT.getType(),
            HomeType.TELEPORT.getType(),
            HomeType.ADD.getType(),
            HomeType.UPDATE.getType(),
            HomeType.REMOVE.getType()
    )),
    ERROR_MAX_HOME("Unable to add more homes."),
    ERROR_INVALID_NAME(
            "Home name must be less than 20 characters, "
                    + "consists of only letters, digits, and dash, "
                    + "and does not start with a digit."
    ),
    ERROR_HOME_NAME_NOT_UNIQUE("Home name exists."),
    ERROR_HOME_DOES_NOT_EXISTS("Home doesn't exists."),
    ERROR_TELEPORTATION_CANCELLED("Teleportation cancelled.");

    private final String message;

    /**
     * Home teleport text.
     *
     * @param message text
     */
    HomeTeleportMessage(String message) {
        this.message = message;
    }

    /**
     * Get text.
     *
     * @return text
     */
    public String getMessage() {
        return message;
    }

}
