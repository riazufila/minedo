package net.minedo.mc.constants.command.message.likemessage;

import net.minedo.mc.constants.command.type.customcommandtype.CustomCommandType;

/**
 * Like command texts.
 */
public enum LikeMessage {

    SUCCESS_LIKE_SENT("Liked %s."),
    SUCCESS_LIKE_RECEIVED("%s liked you."),
    ERROR_USAGE(String.format("Usage: /%s <player>", CustomCommandType.LIKE.getType())),
    ERROR_INVALID_TARGET("Invalid target."),
    ERROR_UNABLE_TO_FIND_PLAYER("Player not found."),
    ERROR_LIKE_SENT_RECENTLY("Exceeded like limit per day.");

    private final String message;

    /**
     * Like command text.
     *
     * @param message text
     */
    LikeMessage(String message) {
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
