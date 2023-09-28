package net.minedo.mc.constants.command.message.ignoremessage;

import net.minedo.mc.constants.command.type.customcommandtype.CustomCommandType;

public enum IgnoreMessage {

    ERROR_USAGE(String.format("Usage: /%s <add | remove> <player>", CustomCommandType.IGNORE.getMessage())),
    ERROR_ALREADY_BLOCKED("%s has already been blocked."),
    ERROR_NOT_BLOCKED("%s isn't blocked."),
    ERROR_INTERACT("%s has blocked you."),
    ERROR_TARGET("Invalid target."),
    ERROR_PLAYER_IS_NOT_IN_SERVER("Player is not in the server."),
    SUCCESS_REMOVE_BLOCK_PLAYER("%s is removed from the block list."),
    SUCCESS_ADD_BLOCKED_PLAYER("%s is added to the block list.");

    private final String message;

    IgnoreMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
