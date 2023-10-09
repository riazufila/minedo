package net.minedo.mc.constants.command.message.whispermessage;

import net.minedo.mc.constants.command.type.customcommandtype.CustomCommandType;

/**
 * Whisper command texts.
 */
public enum WhisperMessage {

    ERROR_USAGE(String.format("Usage: /%s <player> <content>", CustomCommandType.MESSAGE.getType())),
    ERROR_MESSAGE_TO_SELF("Invalid target."),
    ERROR_PLAYER_NOT_FOUND("Player not found.");

    private final String message;

    /**
     * Whisper command text.
     *
     * @param message text
     */
    WhisperMessage(String message) {
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
