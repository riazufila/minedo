package net.minedo.mc.models.customitem;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minedo.mc.models.customitemenchantment.CustomItemEnchantment;
import net.minedo.mc.models.customitemlore.CustomItemLore;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record CustomItem(@NotNull Integer id, @NotNull Material material, @NotNull String displayName,
                         NamedTextColor color, TextDecoration decoration,
                         CustomItemLore lore, List<CustomItemEnchantment> enchantments) {
}
