package net.minedo.mc.constants.command.type.playerteleporttype;

public enum PlayerTeleportType {

    REQUEST("request"),
    ACCEPT("accept"),
    DECLINE("decline"),
    DISCARD("discard");

    private final String type;

    PlayerTeleportType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
