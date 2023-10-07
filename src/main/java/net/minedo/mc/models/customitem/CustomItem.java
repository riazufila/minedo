package net.minedo.mc.models.customitem;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minedo.mc.models.customitemenchantment.CustomItemEnchantment;
import net.minedo.mc.models.customitemlore.CustomItemLore;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class CustomItem {

    private int id;
    private Material material;
    private String displayName;
    private NamedTextColor color;
    private TextDecoration decoration;
    private CustomItemLore lore;
    private List<CustomItemEnchantment> enchantments;

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

    public CustomItemLore getLore() {
        return lore;
    }

    public void setLore(CustomItemLore lore) {
        this.lore = lore;
    }

    public List<CustomItemEnchantment> getEnchantments() {
        return enchantments;
    }

    public void addEnchantment(CustomItemEnchantment enchantment) {
        if (this.enchantments == null) {
            this.enchantments = new ArrayList<>();
        }

        this.enchantments.add(enchantment);
    }

}
