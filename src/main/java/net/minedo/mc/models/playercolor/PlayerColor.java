package net.minedo.mc.models.playercolor;

import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.Nullable;

/**
 * Player color settings.
 *
 * @param prefixPreset  name or prefix color in {@link NamedTextColor#NAMES}
 * @param prefixCustom  name or prefix custom color based on HEX
 * @param contentPreset chat content color in {@link NamedTextColor#NAMES}
 * @param contentCustom chat content custom color based on HEX
 */
public record PlayerColor(@Nullable String prefixPreset,
                          @Nullable String prefixCustom,
                          @Nullable String contentPreset,
                          @Nullable String contentCustom) {
}
