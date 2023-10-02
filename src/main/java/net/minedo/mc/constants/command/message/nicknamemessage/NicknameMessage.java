package net.minedo.mc.constants.command.message.nicknamemessage;

import net.minedo.mc.constants.command.type.customcommandtype.CustomCommandType;
import net.minedo.mc.constants.command.type.nicknametype.NicknameType;

public enum NicknameMessage {

    ERROR_USAGE(String.format(
            "Usage: /%s <%s <nickname> | %s <nickname> | %s>",
            CustomCommandType.NICKNAME.getMessage(),
            NicknameType.SET.getType(),
            NicknameType.REVEAL.getType(),
            NicknameType.REMOVE.getType()
    )),
    ERROR_NO_PERMISSION("Operation not permitted."),
    ERROR_UNABLE_TO_PINPOINT("Unable to reveal the real player's name."),
    ERROR_INVALID_NICKNAME(
            "Nickname must be less than 20 characters, "
                    + "consists of only letters and digits, "
                    + "and does not start with a digit."
    ),
    SUCCESS_SET_NICKNAME("Nickname updated."),
    SUCCESS_REVEAL_NICKNAME("%s's real name is %s."),
    SUCCESS_REMOVE_NICKNAME("Nickname removed.");

    private final String message;

    NicknameMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
