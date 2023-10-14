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
 * Inflicts weakness.
 */
public class WeaknessEnchantmentHandler extends CustomEnchantmentHandler implements Listener {

    /**
     * Initialize weakness enchantment handler.
     */
    public WeaknessEnchantmentHandler() {
        super(CustomEnchantmentType.WEAKNESS);
    }

    @EventHandler
    public void onHit(@NotNull EntityDamageByEntityEvent event) {
        super.triggerCustomEffectsOnHit(event, PotionEffectType.WEAKNESS, true);
    }

    @EventHandler
    public void onPlayerArmorChange(@NotNull PlayerArmorChangeEvent event) {
        super.updateCustomEffectsOnArmorChange(event, PotionEffectType.WEAKNESS);
    }

}
