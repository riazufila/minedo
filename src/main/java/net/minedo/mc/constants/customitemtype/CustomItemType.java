package net.minedo.mc.constants.customitemtype;

public enum CustomItemType {

    CUSTOM_ITEM("custom");

    private final String type;

    CustomItemType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
