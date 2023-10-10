package net.minedo.mc.functionalities.customenchantment.list;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;

/**
 * Grants jump.
 */
public class JumpEnchantmentHandler extends CustomEnchantmentHandler {

    /**
     * Initialize jump enchantment handler.
     */
    public JumpEnchantmentHandler() {
        super(CustomEnchantmentType.JUMP);
    }

    @Override
    @EventHandler
    public void onPlayerArmorChange(PlayerArmorChangeEvent event) {
        super.updateCustomEffectsOnArmorChange(event, PotionEffectType.JUMP);
    }

}
