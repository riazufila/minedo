package net.minedo.mc.constants.command.message.playerteleportmessage;

import net.minedo.mc.constants.command.type.customcommandtype.CustomCommandType;
import net.minedo.mc.constants.command.type.playerteleporttype.PlayerTeleportType;

/**
 * Player teleport command texts.
 */
public enum PlayerTeleportMessage {

    SUCCESS_REQUEST_REQUESTEE("Waiting for %s to respond.."),
    SUCCESS_REQUEST_REQUESTER("%s requested to teleport.."),
    SUCCESS_TELEPORT_REQUESTER("Teleported to %s."),
    SUCCESS_TELEPORT_REQUESTEE("%s teleported to you."),
    INFO_TELEPORTING_REQUESTEE("Stand still while %s is teleporting.."),
    INFO_TELEPORTING_REQUESTER("Teleporting to %s in 5.."),
    INFO_DISCARD_REQUESTEE("%s discarded the teleport request."),
    INFO_DISCARD_REQUESTER("You discarded the teleport request."),
    INFO_COUNTDOWN("%s.."),
    ERROR_USAGE(String.format("Usage: /%s <%s <player> | %s | %s | %s>",
            CustomCommandType.PLAYER_TELEPORT.getType(),
            PlayerTeleportType.REQUEST.getType(),
            PlayerTeleportType.ACCEPT.getType(),
            PlayerTeleportType.DECLINE.getType(),
            PlayerTeleportType.DISCARD.getType()
    )),
    ERROR_REQUEST_SENT_OUT("You already have a teleport request sent out."),
    ERROR_REQUEST_TO_SELF("Invalid target."),
    ERROR_REQUEST_PLAYER_NOT_FOUND("Player not found."),
    ERROR_NO_REQUEST_RECEIVED("No teleport request from any players."),
    ERROR_NO_REQUEST_SENT("No teleport request sent out."),
    ERROR_DECLINED_REQUESTER("%s declined your teleport request."),
    ERROR_DECLINED_REQUESTEE("You declined %s request to teleport."),
    ERROR_OFFLINE_DECLINED_REQUESTEE("You declined the request to teleport."),
    ERROR_TELEPORTATION_CANCELLED("Teleportation cancelled."),
    ERROR_REQUEST_TIMEOUT("Teleport request timeout."),
    ERROR_TELEPORT_TARGET_OFFLINE("%s is offline."),
    ERROR_TELEPORTATION_PROCESS_NOT_FOUND("Teleportation process isn't found.");

    private final String message;

    /**
     * Player teleport command text.
     *
     * @param message text
     */
    PlayerTeleportMessage(String message) {
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
