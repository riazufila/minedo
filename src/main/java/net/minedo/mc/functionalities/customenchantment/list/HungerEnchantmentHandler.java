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
 * Inflicts hunger.
 */
public class HungerEnchantmentHandler extends CustomEnchantmentHandler implements Listener {

    /**
     * Initialize hunger enchantment handler.
     */
    public HungerEnchantmentHandler() {
        super(CustomEnchantmentType.HUNGER);
    }

    @EventHandler
    public void onHit(@NotNull EntityDamageByEntityEvent event) {
        super.triggerPotionEffectsOnHit(event, PotionEffectType.HUNGER, true);
    }

    @EventHandler
    public void onPlayerArmorChange(@NotNull PlayerArmorChangeEvent event) {
        super.updatePotionEffectsOnArmorChange(event, PotionEffectType.HUNGER);
    }

}
