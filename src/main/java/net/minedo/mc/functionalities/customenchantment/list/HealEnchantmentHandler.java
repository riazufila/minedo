package net.minedo.mc.functionalities.customenchantment.list;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * Inflict heal.
 */
public class HealEnchantmentHandler extends CustomEnchantmentHandler implements Listener {

    /**
     * Initialize heal enchantment handler.
     */
    public HealEnchantmentHandler() {
        super(CustomEnchantmentType.HEAL);
    }

    @EventHandler
    public void onHit(@NotNull EntityDamageByEntityEvent event) {
        super.triggerPotionEffectsOnHit(event, PotionEffectType.HEAL, true);
    }

    @EventHandler
    public void onPlayerArmorChange(@NotNull PlayerArmorChangeEvent event) {
        super.updatePotionEffectsOnArmorChange(event, PotionEffectType.HEAL);
    }

}
