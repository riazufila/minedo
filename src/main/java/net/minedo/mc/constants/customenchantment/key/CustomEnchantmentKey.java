package net.minedo.mc.constants.customenchantment.key;

import org.jetbrains.annotations.NotNull;

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
    CustomEnchantmentKey(@NotNull String key) {
        this.key = key;
    }

    /**
     * Get custom enchantment key.
     *
     * @return key
     */
    public @NotNull String getKey() {
        return key;
    }


}
