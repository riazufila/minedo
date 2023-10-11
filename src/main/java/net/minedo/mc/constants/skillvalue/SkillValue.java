package net.minedo.mc.constants.skillvalue;

/**
 * Skill values.
 */
public enum SkillValue {

    MAX_SKILL_POINTS(5);

    private final int value;

    /**
     * Skill value.
     *
     * @param value value
     */
    SkillValue(int value) {
        this.value = value;
    }

    /**
     * Get value
     *
     * @return value
     */
    public int getValue() {
        return value;
    }


}
