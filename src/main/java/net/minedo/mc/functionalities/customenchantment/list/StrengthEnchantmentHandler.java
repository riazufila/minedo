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
 * Grants strength.
 */
public class StrengthEnchantmentHandler extends CustomEnchantmentHandler implements Listener {

    /**
     * Initialize strength enchantment handler.
     */
    public StrengthEnchantmentHandler() {
        super(CustomEnchantmentType.STRENGTH);
    }

    @EventHandler
    public void onHit(@NotNull EntityDamageByEntityEvent event) {
        super.triggerPotionEffectsOnHit(event, PotionEffectType.INCREASE_DAMAGE, true);
    }

    @EventHandler
    public void onPlayerArmorChange(@NotNull PlayerArmorChangeEvent event) {
        super.updatePotionEffectsOnArmorChange(event, PotionEffectType.INCREASE_DAMAGE);
    }

}
