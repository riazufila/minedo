package net.minedo.mc.constants.worldtype;

public enum WorldType {

    WORLD("world"),
    NETHER_WORLD("world_nether"),
    THE_END_WORLD("world_the_end");

    private final String type;

    WorldType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
