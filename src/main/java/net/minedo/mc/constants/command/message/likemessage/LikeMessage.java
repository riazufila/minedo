package net.minedo.mc.constants.command.message.likemessage;

import net.minedo.mc.constants.command.type.customcommandtype.CustomCommandType;

public enum LikeMessage {

    SUCCESS_LIKE_SENT("Sent %s a like."),
    SUCCESS_LIKE_RECEIVED("%s liked you!"),
    ERROR_USAGE(String.format("Usage: /%s <player>", CustomCommandType.LIKE.getMessage())),
    ERROR_INVALID_TARGET("Invalid target."),
    ERROR_REQUEST_PLAYER_IS_NOT_IN_SERVER("Player is not in the server."),
    ERROR_LIKE_SENT_RECENTLY("Only one like can be sent per day.");

    private final String message;

    LikeMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
