package net.minedo.mc.constants.worldtype;

import org.jetbrains.annotations.NotNull;

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
    WorldType(@NotNull String type) {
        this.type = type;
    }

    /**
     * Get world type.
     *
     * @return world type
     */
    public @NotNull String getType() {
        return type;
    }

}
