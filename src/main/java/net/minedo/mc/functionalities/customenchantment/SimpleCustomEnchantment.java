package net.minedo.mc.functionalities.customenchantment;

import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;

public class SimpleCustomEnchantment {

    private CustomEnchantmentType customEnchantmentType;
    private short level;

    public SimpleCustomEnchantment(CustomEnchantmentType customEnchantmentType, short level) {
        this.customEnchantmentType = customEnchantmentType;
        this.level = level;
    }

    public CustomEnchantmentType getCustomEnchantmentType() {
        return customEnchantmentType;
    }

    public void setCustomEnchantmentType(CustomEnchantmentType customEnchantmentType) {
        this.customEnchantmentType = customEnchantmentType;
    }

    public short getLevel() {
        return level;
    }

    public void setLevel(short level) {
        this.level = level;
    }
}
