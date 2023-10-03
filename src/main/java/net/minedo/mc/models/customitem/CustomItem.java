package net.minedo.mc.models.customitem;

import net.kyori.adventure.text.format.TextDecoration;
import net.minedo.mc.models.customitemattribute.CustomItemAttribute;
import net.minedo.mc.models.customitemenchantment.CustomItemEnchantment;
import net.minedo.mc.models.customitemlore.CustomItemLore;
import net.minedo.mc.models.customitemprobability.CustomItemProbability;
import org.bukkit.Material;

import java.util.List;

public class CustomItem {

    private int id;
    private Material material;
    private String displayName;
    private String color;
    private TextDecoration decoration;
    private CustomItemLore lore;
    private List<CustomItemEnchantment> enchantments;
    private List<CustomItemAttribute> attributes;
    private CustomItemProbability probability;

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

    public CustomItemLore getLore() {
        return lore;
    }

    public void setLore(CustomItemLore lore) {
        this.lore = lore;
    }

    public List<CustomItemEnchantment> getEnchantments() {
        return enchantments;
    }

    public void setEnchantments(List<CustomItemEnchantment> enchantments) {
        this.enchantments = enchantments;
    }

    public void addEnchantment(CustomItemEnchantment enchantment) {
        this.enchantments.add(enchantment);
    }

    public List<CustomItemAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<CustomItemAttribute> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(CustomItemAttribute attribute) {
        this.attributes.add(attribute);
    }

    public CustomItemProbability getProbability() {
        return probability;
    }

    public void setProbability(CustomItemProbability probability) {
        this.probability = probability;
    }

}
