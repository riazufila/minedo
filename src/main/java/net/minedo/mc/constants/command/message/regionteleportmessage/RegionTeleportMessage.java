package net.minedo.mc.constants.command.message.regionteleportmessage;

public enum RegionTeleportMessage {

    SUCCESS_TELEPORT("Teleported to %s!"),
    INFO_TELEPORTING("Teleporting to %s in 5.."),
    INFO_COUNTDOWN("%s.."),
    ERROR_USAGE("Usage: /warp <region>"),
    ERROR_TELEPORTATION_CANCELLED("Teleportation cancelled.");

    private final String message;

    RegionTeleportMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
