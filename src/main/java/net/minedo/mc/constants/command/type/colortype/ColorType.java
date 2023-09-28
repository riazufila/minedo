package net.minedo.mc.constants.command.type.colortype;

public enum ColorType {

    NAME("name"),
    CHAT("chat"),
    PRESET("preset"),
    CUSTOM("custom"),
    REMOVE("remove");

    private final String type;

    ColorType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
