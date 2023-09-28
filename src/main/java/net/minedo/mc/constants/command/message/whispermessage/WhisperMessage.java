package net.minedo.mc.constants.command.message.whispermessage;

import net.minedo.mc.constants.command.type.customcommandtype.CustomCommandType;

public enum WhisperMessage {

    ERROR_USAGE(String.format("Usage: /%s <player> <content>", CustomCommandType.MESSAGE.getMessage())),
    ERROR_MESSAGE_TO_SELF("Invalid message target."),
    ERROR_PLAYER_OFFLINE("Player is offline.");

    private final String message;

    WhisperMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
