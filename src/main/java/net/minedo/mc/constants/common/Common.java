package net.minedo.mc.constants.common;

public enum Common {

    CHUNK_SIZE(16),
    ATTACK_COOL_DOWN(1.0f),
    TICK_PER_SECOND(20);

    private final Object value;

    Common(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

}
