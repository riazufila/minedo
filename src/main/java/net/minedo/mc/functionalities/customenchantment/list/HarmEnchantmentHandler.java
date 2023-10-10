package net.minedo.mc.functionalities.customenchantment.list;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * Inflicts harm.
 */
public class HarmEnchantmentHandler extends CustomEnchantmentHandler {

    /**
     * Initialize harm enchantment handler.
     */
    public HarmEnchantmentHandler() {
        super(CustomEnchantmentType.HARM);
    }

    @Override
    @EventHandler
    public void onHit(@NotNull EntityDamageByEntityEvent event) {
        super.triggerCustomEffectsOnHit(event, PotionEffectType.HARM, true);
    }

    @Override
    @EventHandler
    public void onPlayerArmorChange(@NotNull PlayerArmorChangeEvent event) {
        super.updateCustomEffectsOnArmorChange(event, PotionEffectType.HARM);
    }

}
