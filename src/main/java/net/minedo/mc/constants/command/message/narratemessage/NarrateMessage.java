package net.minedo.mc.constants.command.message.narratemessage;

import net.minedo.mc.constants.command.type.customcommandtype.CustomCommandType;

public enum NarrateMessage {

    ERROR_USAGE(String.format("Usage: /%s <message>", CustomCommandType.NARRATE.getMessage()));

    private final String message;

    NarrateMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
