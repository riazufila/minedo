package io.github.riazufila.minedoplugin.betteritem.astralgear;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;

public class AstralGearAttribute {

    public int id;
    public Attribute attribute;
    public double modifier;
    public AttributeModifier.Operation operation;
    public EquipmentSlot slot;
    public int astralGearId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public double getModifier() {
        return modifier;
    }

    public void setModifier(double modifier) {
        this.modifier = modifier;
    }

    public AttributeModifier.Operation getOperation() {
        return operation;
    }

    public void setOperation(AttributeModifier.Operation operation) {
        this.operation = operation;
    }

    public EquipmentSlot getSlot() {
        return slot;
    }

    public void setSlot(EquipmentSlot slot) {
        this.slot = slot;
    }

    public int getAstralGearId() {
        return astralGearId;
    }

    public void setAstralGearId(int astralGearId) {
        this.astralGearId = astralGearId;
    }

}
