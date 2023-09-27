package net.minedo.mc.constants.ignoremessage;

public enum IgnoreMessage {

    ERROR_USAGE("Usage: /ignore <add | remove> <player>"),
    ERROR_ALREADY_BLOCKED("%s has already been blocked."),
    ERROR_NOT_BLOCKED("%s isn't blocked."),
    ERROR_TELEPORT_REQUEST_BLOCKED("%s blocked teleport request from you."),
    ERROR_PLAYER_IS_NOT_IN_SERVER("Player is not in the server."),
    SUCCESS_REMOVE_BLOCK_PLAYER("%s is removed from the block list."),
    SUCCESS_ADD_BLOCKED_PLAYER("%s is added to the block list.");

    private final String message;

    IgnoreMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
