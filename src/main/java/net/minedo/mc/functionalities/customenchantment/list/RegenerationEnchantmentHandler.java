package net.minedo.mc.functionalities.customenchantment.list;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;

/**
 * Grants regeneration.
 */
public class RegenerationEnchantmentHandler extends CustomEnchantmentHandler {

    /**
     * Initialize regeneration enchantment handler.
     */
    public RegenerationEnchantmentHandler() {
        super(CustomEnchantmentType.REGENERATION);
    }

    @Override
    @EventHandler
    public void onPlayerArmorChange(PlayerArmorChangeEvent event) {
        super.updateCustomEffectsOnArmorChange(event, PotionEffectType.REGENERATION);
    }

}
