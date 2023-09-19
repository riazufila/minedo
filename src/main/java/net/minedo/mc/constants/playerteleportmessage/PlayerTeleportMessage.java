package net.minedo.mc.constants.playerteleportmessage;

import net.minedo.mc.constants.playerteleporttype.PlayerTeleportType;

public enum PlayerTeleportMessage {

    SUCCESS_REQUEST_REQUESTEE("Waiting for %s to respond.."),
    SUCCESS_REQUEST_REQUESTER("%s is requesting to teleport.."),
    SUCCESS_TELEPORT_REQUESTER("Teleported to %s!"),
    SUCCESS_TELEPORT_REQUESTEE("%s teleported to you!"),
    INFO_TELEPORTING_REQUESTEE("Stand still while %s is teleporting.."),
    INFO_TELEPORTING_REQUESTER("Teleporting to %s in 5.."),
    INFO_DISCARD_REQUESTEE("%s discarded the teleport request."),
    INFO_DISCARD_REQUESTER("You discarded the teleport request."),
    INFO_COUNTDOWN("%s.."),
    ERROR_USAGE(String.format("Usage: /teleport <%s <player> | %s | %s | %s>",
            PlayerTeleportType.REQUEST.getType(),
            PlayerTeleportType.ACCEPT.getType(),
            PlayerTeleportType.DECLINE.getType(),
            PlayerTeleportType.DISCARD.getType()
    )),
    ERROR_REQUEST_SENT_OUT("You already have a teleport request sent out."),
    ERROR_REQUEST_TO_SELF("Unable to teleport to yourself."),
    ERROR_REQUEST_PLAYER_IS_NOT_IN_SERVER("%s is not in the server."),
    ERROR_NO_TELEPORT_REQUEST_FROM_ANY_PLAYER("You don't have a teleport request from any players."),
    ERROR_NO_REQUEST_RECEIVED("You don't have a teleport request from any players."),
    ERROR_DECLINED_REQUESTER("%s declined your teleport request."),
    ERROR_DECLINED_REQUESTEE("You declined %s request to teleport."),
    ERROR_NO_REQUEST_SENT("You declined %s request to teleport."),
    ERROR_TELEPORTATION_CANCELLED("Teleportation cancelled."),
    ERROR_REQUEST_TIMEOUT("Teleport request timeout."),
    ERROR_TELEPORT_TARGET_OFFLINE("%s is offline.");

    private final String message;

    PlayerTeleportMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
