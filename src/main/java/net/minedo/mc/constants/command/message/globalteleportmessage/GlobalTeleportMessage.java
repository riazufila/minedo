package net.minedo.mc.constants.command.message.globalteleportmessage;

import org.jetbrains.annotations.NotNull;

/**
 * Global teleport texts.
 */
public enum GlobalTeleportMessage {

    ERROR_USE_MORE_THAN_ONCE_AT_A_TIME("Already teleporting.");

    private final String message;

    /**
     * Global teleport text.
     *
     * @param message text
     */
    GlobalTeleportMessage(@NotNull String message) {
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
