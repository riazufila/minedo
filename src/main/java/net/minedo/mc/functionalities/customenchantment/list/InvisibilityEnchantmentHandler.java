package net.minedo.mc.functionalities.customenchantment.list;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;

/**
 * Grants invisibility.
 */
public class InvisibilityEnchantmentHandler extends CustomEnchantmentHandler {

    /**
     * Initialize invisibility enchantment handler.
     */
    public InvisibilityEnchantmentHandler() {
        super(CustomEnchantmentType.INVISIBILITY);
    }

    @Override
    @EventHandler
    public void onPlayerArmorChange(PlayerArmorChangeEvent event) {
        super.updateCustomEffectsOnArmorChange(event, PotionEffectType.INVISIBILITY);
    }

}
