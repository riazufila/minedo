package net.minedo.mc.constants.command.message.narratemessage;

import net.minedo.mc.constants.command.type.customcommandtype.CustomCommandType;

/**
 * Narrate command texts.
 */
public enum NarrateMessage {

    ERROR_USAGE(String.format("Usage: /%s <message>", CustomCommandType.NARRATE.getType()));

    private final String message;

    /**
     * Narrate command text.
     *
     * @param message text
     */
    NarrateMessage(String message) {
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
