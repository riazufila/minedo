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
 * Grants resistance.
 */
public class ResistanceEnchantmentHandler extends CustomEnchantmentHandler implements Listener {

    /**
     * Initialize resistance enchantment handler.
     */
    public ResistanceEnchantmentHandler() {
        super(CustomEnchantmentType.DAMAGE_RESISTANCE);
    }

    @EventHandler
    public void onHit(@NotNull EntityDamageByEntityEvent event) {
        super.triggerCustomEffectsOnHit(event, PotionEffectType.DAMAGE_RESISTANCE, true);
    }

    @EventHandler
    public void onPlayerArmorChange(@NotNull PlayerArmorChangeEvent event) {
        super.updateCustomEffectsOnArmorChange(event, PotionEffectType.DAMAGE_RESISTANCE);
    }

}
