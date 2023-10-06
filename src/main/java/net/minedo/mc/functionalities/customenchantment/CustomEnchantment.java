package net.minedo.mc.functionalities.customenchantment;

import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;

public class CustomEnchantment extends SimpleCustomEnchantment {

    private short level;

    public CustomEnchantment(CustomEnchantmentType customEnchantmentType, short level) {
        super(customEnchantmentType);
        this.level = level;
    }

    public short getLevel() {
        return level;
    }

    public void setLevel(short level) {
        this.level = level;
    }

}
