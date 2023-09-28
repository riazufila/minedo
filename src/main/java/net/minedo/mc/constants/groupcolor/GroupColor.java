package net.minedo.mc.constants.groupcolor;

import net.kyori.adventure.text.format.NamedTextColor;

public enum GroupColor {

    GOLD(NamedTextColor.GOLD),
    EMERALD(NamedTextColor.GREEN),
    DIAMOND(NamedTextColor.DARK_AQUA),
    REDSTONE(NamedTextColor.DARK_RED),
    OBSIDIAN(NamedTextColor.DARK_PURPLE);

    private final NamedTextColor color;

    GroupColor(NamedTextColor color) {
        this.color = color;
    }

    public NamedTextColor getColor() {
        return color;
    }

}
