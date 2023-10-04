package net.minedo.mc.functionalities.customenchantment;

import io.papermc.paper.enchantments.EnchantmentRarity;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public abstract class CustomEnchantment extends Enchantment implements Listener {

    public CustomEnchantment(@NotNull NamespacedKey key) {
        super(key);
    }

    public abstract @NotNull String getName();

    public abstract int getMaxLevel();

    @Override
    public int getStartLevel() {
        return 1;
    }

    public abstract @NotNull EnchantmentTarget getItemTarget();

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    public abstract boolean conflictsWith(@NotNull Enchantment other);

    public abstract boolean canEnchantItem(@NotNull ItemStack item);

    public abstract @NotNull Component displayName(int level);

    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }

    @Override
    public @NotNull EnchantmentRarity getRarity() {
        return EnchantmentRarity.VERY_RARE;
    }

    public abstract float getDamageIncrease(int level, @NotNull EntityCategory entityCategory);

    public abstract @NotNull Set<EquipmentSlot> getActiveSlots();

    public abstract @NotNull String translationKey();

    public abstract @NotNull Key key();

    @EventHandler
    public abstract void onHit(EntityDamageByEntityEvent event);

    @EventHandler
    public abstract void onDamaged(EntityDamageEvent event);

    @EventHandler
    public abstract void onInteract(PlayerInteractEvent event);

}
