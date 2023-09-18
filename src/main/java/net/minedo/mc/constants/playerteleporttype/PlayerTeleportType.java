package net.minedo.mc.constants.playerteleporttype;

public enum PlayerTeleportType {

    REQUEST("request"),
    ACCEPT("accept"),
    DECLINE("decline");

    private final String type;

    PlayerTeleportType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
