package net.minedo.mc.functionalities.customenchantment;

import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;

public class SimpleCustomEnchantment {

    private final CustomEnchantmentType customEnchantmentType;

    public SimpleCustomEnchantment(CustomEnchantmentType customEnchantmentType) {
        this.customEnchantmentType = customEnchantmentType;
    }

    public CustomEnchantmentType getCustomEnchantmentType() {
        return customEnchantmentType;
    }

}
