package net.minedo.mc.models.customitemenchantment;

import org.bukkit.enchantments.Enchantment;

public class CustomItemEnchantment {

    private int id;
    private Enchantment enchantment;
    private int level;
    private int customItemId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }

    public void setEnchantment(Enchantment enchantment) {
        this.enchantment = enchantment;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getCustomItemId() {
        return customItemId;
    }

    public void setCustomItemId(int customItemId) {
        this.customItemId = customItemId;
    }

}
