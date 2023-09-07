package io.github.riazufila.minedoplugin.constants.worldtype;

public enum WorldType {
    WORLD("world");

    private final String type;

    WorldType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
