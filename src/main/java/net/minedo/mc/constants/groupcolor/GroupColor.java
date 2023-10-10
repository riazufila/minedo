package net.minedo.mc.constants.groupcolor;

import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.Nullable;

/**
 * Color based on permission.
 */
public enum GroupColor {

    DEFAULT(null),
    GOLD(NamedTextColor.GOLD),
    EMERALD(NamedTextColor.GREEN),
    DIAMOND(NamedTextColor.DARK_AQUA),
    REDSTONE(NamedTextColor.DARK_RED),
    OBSIDIAN(NamedTextColor.DARK_PURPLE);

    private final NamedTextColor color;

    /**
     * Color based on permission.
     *
     * @param color color values as in {@link NamedTextColor#NAMES}
     */
    GroupColor(@Nullable NamedTextColor color) {
        this.color = color;
    }

    /**
     * Get color.
     *
     * @return color
     */
    public @Nullable NamedTextColor getColor() {
        return color;
    }

}
