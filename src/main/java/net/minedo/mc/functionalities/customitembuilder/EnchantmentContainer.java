package net.minedo.mc.functionalities.customitembuilder;

import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

/**
 * Contain information of vanilla enchantments.
 *
 * @param enchantment enchantment
 * @param level       level
 */
public record EnchantmentContainer(@NotNull Enchantment enchantment,
                                   int level) {
}
