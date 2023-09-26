package net.minedo.mc.constants.likemessage;

public enum LikeMessage {

    SUCCESS_LIKE_SENT("Sent %s a like."),
    SUCCESS_LIKE_RECEIVED("%s sent you a like!"),
    ERROR_USAGE("Usage: /like <player>"),
    ERROR_REQUEST_PLAYER_IS_NOT_IN_SERVER("%s is not in the server.");

    private final String message;

    LikeMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
