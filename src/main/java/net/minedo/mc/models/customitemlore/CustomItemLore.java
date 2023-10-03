package net.minedo.mc.models.customitemlore;

import net.kyori.adventure.text.format.TextDecoration;

public class CustomItemLore {

    private int id;
    private String text;
    private String color;
    private TextDecoration decoration;
    private int customItemId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public TextDecoration getDecoration() {
        return decoration;
    }

    public void setDecoration(TextDecoration decoration) {
        this.decoration = decoration;
    }

    public int getCustomItemId() {
        return customItemId;
    }

    public void setCustomItemId(int customItemId) {
        this.customItemId = customItemId;
    }

}
