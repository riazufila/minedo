package net.minedo.mc.constants.command.message.whispermessage;

public enum WhisperMessage {

    ERROR_USAGE("Usage: /message <player>"),
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
