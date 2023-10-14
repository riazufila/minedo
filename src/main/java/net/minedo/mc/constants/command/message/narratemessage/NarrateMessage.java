package net.minedo.mc.constants.command.message.narratemessage;

import net.minedo.mc.constants.command.type.customcommandtype.CustomCommandType;
import org.jetbrains.annotations.NotNull;

/**
 * Narrate command texts.
 */
public enum NarrateMessage {

    ERROR_USAGE(String.format("Usage: /%s <message>", CustomCommandType.NARRATE.getType()));

    private final String message;

    /**
     * Narrate command text.
     *
     * @param message text
     */
    NarrateMessage(@NotNull String message) {
        this.message = message;
    }

    /**
     * Get text.
     *
     * @return text
     */
    public @NotNull String getMessage() {
        return message;
    }

}
