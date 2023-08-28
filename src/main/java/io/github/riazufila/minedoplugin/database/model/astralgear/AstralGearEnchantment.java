package io.github.riazufila.minedoplugin.database.model.astralgear;

import org.bukkit.enchantments.Enchantment;

public class AstralGearEnchantment {

    public int id;
    public Enchantment enchantment;
    public int level;
    public int astralGearId;

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

    public int getAstralGearId() {
        return astralGearId;
    }

    public void setAstralGearId(int astralGearId) {
        this.astralGearId = astralGearId;
    }

}
