package net.minedo.mc.functionalities.customenchantment.list;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;

/**
 * Grants haste.
 */
public class HasteEnchantmentHandler extends CustomEnchantmentHandler {

    /**
     * Initialize haste enchantment handler.
     */
    public HasteEnchantmentHandler() {
        super(CustomEnchantmentType.HASTE);
    }

    @Override
    @EventHandler
    public void onPlayerArmorChange(PlayerArmorChangeEvent event) {
        super.updateCustomEffectsOnArmorChange(event, PotionEffectType.FAST_DIGGING);
    }

}
