package net.minedo.mc.functionalities.customenchantment.list;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;

/**
 * Grants water breathing.
 */
public class WaterBreathingEnchantmentHandler extends CustomEnchantmentHandler {

    /**
     * Initialize water breathing enchantment handler.
     */
    public WaterBreathingEnchantmentHandler() {
        super(CustomEnchantmentType.WATER_BREATHING);
    }

    @Override
    @EventHandler
    public void onPlayerArmorChange(PlayerArmorChangeEvent event) {
        super.updateCustomEffectsOnArmorChange(event, PotionEffectType.WATER_BREATHING);
    }

}
