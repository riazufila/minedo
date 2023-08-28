package io.github.riazufila.minedoplugin.betteritem.astralgear;

import net.kyori.adventure.text.format.TextDecoration;

public class AstralGearLore {

    public int id;
    public String text;
    public String color;
    public TextDecoration decoration;
    public int astralGearId;

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

    public int getAstralGearId() {
        return astralGearId;
    }

    public void setAstralGearId(int astralGearId) {
        this.astralGearId = astralGearId;
    }

}
