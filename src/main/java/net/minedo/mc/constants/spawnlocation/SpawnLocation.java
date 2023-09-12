package net.minedo.mc.constants.spawnlocation;

public enum SpawnLocation {
    POSITION_X(0),
    POSITION_Z(0);

    private final int position;

    SpawnLocation(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
