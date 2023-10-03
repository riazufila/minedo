package net.minedo.mc.constants.command.type.hometype;

public enum HomeType {

    TELEPORT("teleport"),
    ADD("add"),
    UPDATE("update"),
    REMOVE("remove");

    private final String type;

    HomeType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
