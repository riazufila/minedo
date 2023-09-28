package net.minedo.mc.constants.command.message.colormessage;

import net.minedo.mc.constants.command.type.colortype.ColorType;
import net.minedo.mc.constants.command.type.customcommandtype.CustomCommandType;

public enum ColorMessage {

    ERROR_USAGE(String.format(
            "Usage: /%s <%s | %s> <%s | %s> <color (preset or HEX)>",
            CustomCommandType.COLOR.getMessage(),
            ColorType.NAME.getType(),
            ColorType.CHAT.getType(),
            ColorType.PRESET.getType(),
            ColorType.CUSTOM.getType()
    )),
    ERROR_NO_PERMISSION("Operation not allowed."),
    SUCCESS_COLOR_UPDATE("Color settings updated.");

    private final String message;

    ColorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
