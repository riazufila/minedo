package net.minedo.mc.functionalities.customenchantment;

import net.minedo.mc.Minedo;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.dataembedder.DataEmbedder;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class CustomEnchantmentUtils {

    public static boolean isCustomEnchantmentInItem(
            Minedo pluginInstance, ItemStack item, CustomEnchantmentType customEnchantmentType
    ) {
        DataEmbedder dataEmbedder = new DataEmbedder(pluginInstance);
        List<SimpleCustomEnchantment> simpleCustomEnchantments = dataEmbedder
                .getCustomEnchantments(item);

        if (simpleCustomEnchantments != null && !simpleCustomEnchantments.isEmpty()) {
            return simpleCustomEnchantments
                    .stream()
                    .anyMatch(enchantment -> enchantment.getCustomEnchantmentType().equals(customEnchantmentType));
        }

        return false;
    }

}
