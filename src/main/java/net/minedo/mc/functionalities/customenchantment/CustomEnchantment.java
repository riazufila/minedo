package net.minedo.mc.functionalities.customenchantment;

import net.minedo.mc.Minedo;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class CustomEnchantment extends SimpleCustomEnchantment implements Listener {

    private Minedo pluginInstance;
    private String lore;

    public CustomEnchantment(
            CustomEnchantmentType customEnchantmentType, short level, Minedo pluginInstance, String lore
    ) {
        super(customEnchantmentType, level);
        this.pluginInstance = pluginInstance;
        this.lore = lore;
    }

    public Minedo getPluginInstance() {
        return pluginInstance;
    }

    public void setPluginInstance(Minedo pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    public String getLore() {
        return lore;
    }

    public void setLore(String lore) {
        this.lore = lore;
    }

    public void onHit(EntityDamageByEntityEvent event) {
        // Override and add custom effects if needed.
    }

    public void onDamaged(EntityDamageEvent event) {
        // Override and add custom effects if needed.
    }

    public void onInteract(PlayerInteractEvent event) {
        // Override and add custom effects if needed.
    }

}
