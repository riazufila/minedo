package net.minedo.mc.models.customitemlore;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class CustomItemLore {

    private String text;
    private NamedTextColor color;
    private TextDecoration decoration;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public NamedTextColor getColor() {
        return color;
    }

    public void setColor(NamedTextColor color) {
        this.color = color;
    }

    public TextDecoration getDecoration() {
        return decoration;
    }

    public void setDecoration(TextDecoration decoration) {
        this.decoration = decoration;
    }

}
