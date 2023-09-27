package net.minedo.mc.constants.globalteleportmessage;

public enum GlobalTeleportMessage {

    ERROR_USE_MORE_THAN_ONCE_AT_A_TIME("Not allowed to perform multiple teleportation at a time.");

    private final String message;

    GlobalTeleportMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
