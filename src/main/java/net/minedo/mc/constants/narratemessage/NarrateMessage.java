package net.minedo.mc.constants.narratemessage;

public enum NarrateMessage {

    ERROR_USAGE("Usage: /narrate <message>");

    private final String message;

    NarrateMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
