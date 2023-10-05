package net.minedo.mc.functionalities.customenchantment;

import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.dataembedder.DataEmbedder;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class CustomEnchantmentUtils {

    public static boolean isCustomEnchantmentInItem(
            ItemStack item, CustomEnchantmentType customEnchantmentType
    ) {
        List<SimpleCustomEnchantment> simpleCustomEnchantments = DataEmbedder.getCustomEnchantments(item);

        if (simpleCustomEnchantments != null && !simpleCustomEnchantments.isEmpty()) {
            return simpleCustomEnchantments
                    .stream()
                    .anyMatch(enchantment -> enchantment.getCustomEnchantmentType().equals(customEnchantmentType));
        }

        return false;
    }

}
