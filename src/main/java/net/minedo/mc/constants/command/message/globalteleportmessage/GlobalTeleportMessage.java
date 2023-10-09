package net.minedo.mc.constants.command.message.globalteleportmessage;

/**
 * Global teleport texts.
 */
public enum GlobalTeleportMessage {

    ERROR_USE_MORE_THAN_ONCE_AT_A_TIME("Not allowed to perform multiple teleportation at a time.");

    private final String message;

    /**
     * Global teleport text.
     *
     * @param message text
     */
    GlobalTeleportMessage(String message) {
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
