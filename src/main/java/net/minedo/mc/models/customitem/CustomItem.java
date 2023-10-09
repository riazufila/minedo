package net.minedo.mc.models.customitem;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minedo.mc.models.customitemenchantment.CustomItemEnchantment;
import net.minedo.mc.models.customitemlore.CustomItemLore;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Custom items.
 *
 * @param material     material for the custom item
 * @param displayName  custom item's display name
 * @param color        colors as in {@link NamedTextColor#NAMES}
 * @param decoration   decorations as in {@link TextDecoration#NAMES}
 * @param lore         custom item lore
 * @param enchantments list of vanilla or custom enchantments
 */
public record CustomItem(@NotNull Material material,
                         @NotNull String displayName,
                         @Nullable NamedTextColor color,
                         @Nullable TextDecoration decoration,
                         @Nullable CustomItemLore lore,
                         @Nullable List<CustomItemEnchantment> enchantments) {
}
