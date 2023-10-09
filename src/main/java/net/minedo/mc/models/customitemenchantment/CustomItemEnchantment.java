package net.minedo.mc.models.customitemenchantment;

import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

/**
 * Vanilla or custom enchantment used for custom items.
 *
 * @param enchantment enchantment can either be values from {@link Enchantment#values()}
 *                    or {@link CustomEnchantmentType#values()}
 * @param level       level for the enchantment
 */
public record CustomItemEnchantment(@NotNull String enchantment,
                                    int level) {
}
