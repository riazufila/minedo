package net.minedo.mc.functionalities.customenchantment;

import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import org.jetbrains.annotations.NotNull;

/**
 * Custom enchantment information to be stored inside custom item.
 */
public class CustomEnchantment extends SimpleCustomEnchantment {

    private final short level;

    /**
     * Custom enchantment.
     *
     * @param customEnchantmentType custom enchantment value as in {@link CustomEnchantmentType#values()}
     * @param level                 enchantment level
     */
    public CustomEnchantment(@NotNull CustomEnchantmentType customEnchantmentType, short level) {
        super(customEnchantmentType);
        this.level = level;
    }

    /**
     * Get enchantment level.
     *
     * @return enchantment level
     */
    public short getLevel() {
        return level;
    }

}
