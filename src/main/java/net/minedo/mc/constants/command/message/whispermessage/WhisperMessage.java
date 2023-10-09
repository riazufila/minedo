package net.minedo.mc.constants.command.message.whispermessage;

import net.minedo.mc.constants.command.type.customcommandtype.CustomCommandType;

/**
 * Whisper command texts.
 */
public enum WhisperMessage {

    ERROR_USAGE(String.format("Usage: /%s <player> <content>", CustomCommandType.MESSAGE.getType())),
    ERROR_MESSAGE_TO_SELF("Invalid message target."),
    ERROR_PLAYER_OFFLINE("Player is offline.");

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
