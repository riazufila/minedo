package net.minedo.mc.constants.command.message.nicknamemessage;

import net.minedo.mc.constants.command.type.customcommandtype.CustomCommandType;
import net.minedo.mc.constants.command.type.nicknametype.NicknameType;

/**
 * Nickname command texts.
 */
public enum NicknameMessage {

    ERROR_USAGE(String.format(
            "Usage: /%s <%s <nickname> | %s <nickname> | %s>",
            CustomCommandType.NICKNAME.getType(),
            NicknameType.SET.getType(),
            NicknameType.REVEAL.getType(),
            NicknameType.REMOVE.getType()
    )),
    ERROR_NO_PERMISSION("Operation not permitted."),
    ERROR_NO_SUCH_NICKNAME("Currently, there's no player with that nickname."),
    ERROR_UNABLE_TO_PINPOINT("Unable to reveal the real player's name."),
    ERROR_REVEAL_SELF("Invalid target."),
    ERROR_INVALID_NICKNAME(
            "Nickname must be less than 20 characters, "
                    + "consists of only letters and digits, "
                    + "and does not start with a digit."
    ),
    INFO_NICKNAME_TAKEN("Nickname taken."),
    SUCCESS_SET_NICKNAME("Nickname updated."),
    SUCCESS_REVEAL_NICKNAME("%s's real name is %s."),
    SUCCESS_REMOVE_NICKNAME("Nickname removed.");

    private final String message;

    /**
     * Nickname command text.
     *
     * @param message text
     */
    NicknameMessage(String message) {
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
