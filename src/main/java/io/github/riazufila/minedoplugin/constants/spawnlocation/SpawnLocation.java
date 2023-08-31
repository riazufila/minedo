package io.github.riazufila.minedoplugin.constants.spawnlocation;

public enum SpawnLocation {
    positionX(0),
    positionZ(0);

    private final int position;

    SpawnLocation(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
