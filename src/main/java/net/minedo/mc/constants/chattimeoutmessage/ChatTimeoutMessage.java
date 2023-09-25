package net.minedo.mc.constants.chattimeoutmessage;

public enum ChatTimeoutMessage {

    INFO_CHAT_ENABLED("Chat enabled."),
    ERROR_CHAT_TIMEOUT("You're sending too much message!");

    private final String message;

    ChatTimeoutMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
