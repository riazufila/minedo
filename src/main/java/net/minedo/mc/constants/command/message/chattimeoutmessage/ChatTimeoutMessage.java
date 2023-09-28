package net.minedo.mc.constants.command.message.chattimeoutmessage;

public enum ChatTimeoutMessage {

    INFO_CHAT_ENABLED("Chat enabled."),
    ERROR_CHAT_TIMEOUT("Chat timeout.");

    private final String message;

    ChatTimeoutMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}