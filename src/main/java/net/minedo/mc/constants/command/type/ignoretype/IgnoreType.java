package net.minedo.mc.constants.command.type.ignoretype;

public enum IgnoreType {

    ADD("add"),
    REMOVE("remove");

    private final String type;

    IgnoreType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
