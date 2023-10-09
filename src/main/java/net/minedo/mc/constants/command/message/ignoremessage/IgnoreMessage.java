package net.minedo.mc.constants.command.message.ignoremessage;

import net.minedo.mc.constants.command.type.customcommandtype.CustomCommandType;

/**
 * Ignore command texts.
 */
public enum IgnoreMessage {

    ERROR_USAGE(String.format("Usage: /%s <add | remove> <player>", CustomCommandType.IGNORE.getType())),
    ERROR_ALREADY_BLOCKED("%s is already blocked."),
    ERROR_NOT_BLOCKED("%s isn't blocked."),
    ERROR_INTERACT("%s has blocked you."),
    ERROR_TARGET("Invalid target."),
    ERROR_UNABLE_TO_FIND_PLAYER("Player not found."),
    SUCCESS_REMOVE_BLOCK_PLAYER("Removed %s from the block list."),
    SUCCESS_ADD_BLOCKED_PLAYER("Added %s to the block list.");

    private final String message;

    /**
     * Ignore command text.
     *
     * @param message text
     */
    IgnoreMessage(String message) {
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
