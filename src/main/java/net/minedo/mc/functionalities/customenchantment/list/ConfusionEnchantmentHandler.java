package net.minedo.mc.functionalities.customenchantment.list;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * Inflicts confusion.
 */
public class ConfusionEnchantmentHandler extends CustomEnchantmentHandler {

    /**
     * Initialize confusion enchantment handler.
     */
    public ConfusionEnchantmentHandler() {
        super(CustomEnchantmentType.CONFUSION);
    }

    @Override
    @EventHandler
    public void onHit(@NotNull EntityDamageByEntityEvent event) {
        super.triggerCustomEffectsOnHit(event, PotionEffectType.CONFUSION, false);
    }

    @Override
    @EventHandler
    public void onPlayerArmorChange(@NotNull PlayerArmorChangeEvent event) {
        super.updateCustomEffectsOnArmorChange(event, PotionEffectType.CONFUSION);
    }

}
