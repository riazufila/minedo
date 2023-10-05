package net.minedo.mc.functionalities.customenchantment;

import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class CustomEnchantment extends SimpleCustomEnchantment implements Listener {

    private String lore;

    public CustomEnchantment(
            CustomEnchantmentType customEnchantmentType, short level, String lore
    ) {
        super(customEnchantmentType, level);
        this.lore = lore;
    }

    public String getLore() {
        return lore;
    }

    public void setLore(String lore) {
        this.lore = lore;
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        // Override and add custom effects if needed.
    }

    @EventHandler
    public void onDamaged(EntityDamageEvent event) {
        // Override and add custom effects if needed.
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        // Override and add custom effects if needed.
    }

}
