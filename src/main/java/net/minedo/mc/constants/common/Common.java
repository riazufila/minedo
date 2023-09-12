package net.minedo.mc.constants.common;

public enum Common {
    CHUNK_SIZE(16);

    private final int value;

    Common(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
