package net.minedo.mc.constants.spawnlocation;

/**
 * Spawn location.
 */
public enum SpawnLocation {

    POSITION_X(0),
    POSITION_Z(0);

    private final int position;

    /**
     * Spawn location.
     *
     * @param position position
     */
    SpawnLocation(int position) {
        this.position = position;
    }

    /**
     * Get spawn position
     *
     * @return position
     */
    public int getPosition() {
        return position;
    }

}
