package net.minedo.mc.constants.common;

public enum Common {

    CHUNK_SIZE(16),
    ATTACK_COOL_DOWN(1.0f);

    private final Object value;

    Common(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

}
