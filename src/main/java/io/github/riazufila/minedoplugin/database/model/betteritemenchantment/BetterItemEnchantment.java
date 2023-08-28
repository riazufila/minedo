package io.github.riazufila.minedoplugin.database.model.betteritemenchantment;

import org.bukkit.enchantments.Enchantment;

public class BetterItemEnchantment {

    public int id;
    public Enchantment enchantment;
    public int level;
    public int betterItemId;

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

    public int getBetterItemId() {
        return betterItemId;
    }

    public void setBetterItemId(int betterItemId) {
        this.betterItemId = betterItemId;
    }

}
