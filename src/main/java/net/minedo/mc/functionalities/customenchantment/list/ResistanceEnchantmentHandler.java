package net.minedo.mc.functionalities.customenchantment.list;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;

/**
 * Grants resistance.
 */
public class ResistanceEnchantmentHandler extends CustomEnchantmentHandler {

    /**
     * Initialize resistance enchantment handler.
     */
    public ResistanceEnchantmentHandler() {
        super(CustomEnchantmentType.DAMAGE_RESISTANCE);
    }

    @Override
    @EventHandler
    public void onPlayerArmorChange(PlayerArmorChangeEvent event) {
        super.updateCustomEffectsOnArmorChange(event, PotionEffectType.DAMAGE_RESISTANCE);
    }

}
