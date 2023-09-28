package net.minedo.mc.constants.command.message.colormessage;

public enum ColorMessage {

    ERROR_USAGE("Usage: /color <name | chat> <preset | custom> <color (preset or HEX)>"),
    ERROR_NO_PERMISSION("Operation not allowed.");

    private final String message;

    ColorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}