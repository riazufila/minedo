package net.minedo.mc.constants.command.message.chattimeoutmessage;

import org.jetbrains.annotations.NotNull;

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
    ChatTimeoutMessage(@NotNull String message) {
        this.message = message;
    }

    /**
     * Get text.
     *
     * @return text
     */
    public @NotNull String getMessage() {
        return message;
    }

}
