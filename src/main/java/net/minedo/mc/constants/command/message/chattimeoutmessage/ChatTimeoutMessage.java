package net.minedo.mc.constants.command.message.chattimeoutmessage;

/**
 * Chat timeout texts.
 */
public enum ChatTimeoutMessage {

    INFO_CHAT_ENABLED("Chat enabled."),
    ERROR_CHAT_TIMEOUT("Chat timeout.");

    private final String message;

    /**
     * Chat timeout text.
     *
     * @param message text
     */
    ChatTimeoutMessage(String message) {
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
