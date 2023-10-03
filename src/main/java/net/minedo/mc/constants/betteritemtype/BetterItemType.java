package net.minedo.mc.constants.betteritemtype;

public enum BetterItemType {

    CUSTOM("custom");

    private final String type;

    BetterItemType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
