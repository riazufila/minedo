package net.minedo.mc.models.betteritem;

import net.kyori.adventure.text.format.TextDecoration;
import net.minedo.mc.models.betteritemattribute.BetterItemAttribute;
import net.minedo.mc.models.betteritemenchantment.BetterItemEnchantment;
import net.minedo.mc.models.betteritemprobability.BetterItemProbability;
import net.minedo.mc.models.betteritemlore.BetterItemLore;
import org.bukkit.Material;

import java.util.List;

public class BetterItem {

    private int id;
    private Material material;
    private String displayName;
    private String color;
    private TextDecoration decoration;
    private BetterItemLore lore;
    private List<BetterItemEnchantment> enchantments;
    private List<BetterItemAttribute> attributes;
    private BetterItemProbability probability;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public BetterItemLore getLore() {
        return lore;
    }

    public void setLore(BetterItemLore lore) {
        this.lore = lore;
    }

    public List<BetterItemEnchantment> getEnchantments() {
        return enchantments;
    }

    public void setEnchantments(List<BetterItemEnchantment> enchantments) {
        this.enchantments = enchantments;
    }

    public void addEnchantment(BetterItemEnchantment enchantment) {
        this.enchantments.add(enchantment);
    }

    public List<BetterItemAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<BetterItemAttribute> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(BetterItemAttribute attribute) {
        this.attributes.add(attribute);
    }

    public BetterItemProbability getProbability() {
        return probability;
    }

    public void setProbability(BetterItemProbability probability) {
        this.probability = probability;
    }

}
