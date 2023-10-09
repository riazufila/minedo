package net.minedo.mc.constants.worldtype;

/**
 * World type.
 */
public enum WorldType {

    WORLD("world"),
    NETHER_WORLD("world_nether"),
    THE_END_WORLD("world_the_end");

    private final String type;

    /**
     * World type.
     *
     * @param type type
     */
    WorldType(String type) {
        this.type = type;
    }

    /**
     * Get world type.
     *
     * @return world type
     */
    public String getType() {
        return type;
    }

}
