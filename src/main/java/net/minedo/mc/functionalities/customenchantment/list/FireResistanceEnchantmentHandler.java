package net.minedo.mc.functionalities.customenchantment.list;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;

/**
 * Grants fire resistance.
 */
public class FireResistanceEnchantmentHandler extends CustomEnchantmentHandler {

    /**
     * Initialize fire resistance enchantment handler.
     */
    public FireResistanceEnchantmentHandler() {
        super(CustomEnchantmentType.FIRE_RESISTANCE);
    }

    @Override
    @EventHandler
    public void onPlayerArmorChange(PlayerArmorChangeEvent event) {
        super.updateCustomEffectsOnArmorChange(event, PotionEffectType.FIRE_RESISTANCE);
    }

}
