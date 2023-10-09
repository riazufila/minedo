package net.minedo.mc.functionalities.customenchantment;

import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;

/**
 * Simplified custom enchantment. Used to be extended to form other classes.
 */
public class SimpleCustomEnchantment {

    private final CustomEnchantmentType customEnchantmentType;

    /**
     * Initialize simple custom enchantment.
     *
     * @param customEnchantmentType custom enchantment type as in {@link CustomEnchantmentType#values()}
     */
    public SimpleCustomEnchantment(CustomEnchantmentType customEnchantmentType) {
        this.customEnchantmentType = customEnchantmentType;
    }

    /**
     * Get custom enchantment type
     *
     * @return custom enchantment type
     */
    public CustomEnchantmentType getCustomEnchantmentType() {
        return customEnchantmentType;
    }

}
