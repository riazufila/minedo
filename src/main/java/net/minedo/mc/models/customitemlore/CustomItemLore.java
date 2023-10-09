package net.minedo.mc.models.customitemlore;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

public record CustomItemLore(@NotNull String text, NamedTextColor color, TextDecoration decoration) {
}
