package net.minedo.mc.constants.command.message.colormessage;

import net.minedo.mc.constants.command.type.colortype.ColorType;
import net.minedo.mc.constants.command.type.customcommandtype.CustomCommandType;

/**
 * Color command texts.
 */
public enum ColorMessage {

    ERROR_USAGE(String.format(
            "Usage: /%s <%s | %s> <%s | %s> <color (preset or HEX) | %s>",
            CustomCommandType.COLOR.getType(),
            ColorType.NAME.getType(),
            ColorType.CHAT.getType(),
            ColorType.PRESET.getType(),
            ColorType.CUSTOM.getType(),
            ColorType.REMOVE.getType()
    )),
    ERROR_NO_PERMISSION("Operation denied."),
    SUCCESS_COLOR_UPDATE("Color settings updated.");

    private final String message;

    /**
     * Color command text.
     *
     * @param message text
     */
    ColorMessage(String message) {
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
