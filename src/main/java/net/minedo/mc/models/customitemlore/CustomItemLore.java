package net.minedo.mc.models.customitemlore;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Custom item lore.
 *
 * @param text       text for the lore
 * @param color      colors as in {@link NamedTextColor#NAMES}
 * @param decoration decorations as in {@link TextDecoration#NAMES}
 */
public record CustomItemLore(@NotNull String text,
                             @Nullable NamedTextColor color,
                             @Nullable TextDecoration decoration) {
}
