package net.minedo.mc.constants.common;

/**
 * Common values.
 */
public enum Common {

    CHUNK_SIZE(16),
    ATTACK_COOL_DOWN(1.0f),
    TICK_PER_SECOND(20);

    private final Object value;

    /**
     * Common.
     *
     * @param value common value
     */
    Common(Object value) {
        this.value = value;
    }

    /**
     * Get common value.
     *
     * @return common value
     */
    public Object getValue() {
        return value;
    }

}
