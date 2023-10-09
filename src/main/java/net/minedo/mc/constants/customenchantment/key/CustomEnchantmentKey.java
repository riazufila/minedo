package net.minedo.mc.constants.customenchantment.key;

/**
 * Custom enchantment keys.
 */
public enum CustomEnchantmentKey {

    CUSTOM_ENCHANTMENT("custom-enchantment"),
    CUSTOM_ENCHANTMENT_ID("id"),
    CUSTOM_ENCHANTMENT_LEVEL("lvl");

    private final String key;

    /**
     * Custom enchantment key.
     *
     * @param key key
     */
    CustomEnchantmentKey(String key) {
        this.key = key;
    }

    /**
     * Get custom enchantment key.
     *
     * @return key
     */
    public String getKey() {
        return key;
    }


}
