package net.minedo.mc.constants.groupcolor;

import net.kyori.adventure.text.format.NamedTextColor;

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
    GroupColor(NamedTextColor color) {
        this.color = color;
    }

    /**
     * Get color.
     *
     * @return color
     */
    public NamedTextColor getColor() {
        return color;
    }

}
