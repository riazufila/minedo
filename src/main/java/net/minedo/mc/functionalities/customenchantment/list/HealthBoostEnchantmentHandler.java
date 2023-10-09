package net.minedo.mc.functionalities.customenchantment.list;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;

public class HealthBoostEnchantmentHandler extends CustomEnchantmentHandler {

    public HealthBoostEnchantmentHandler() {
        super(CustomEnchantmentType.HEALTH_BOOST);
    }

    @Override
    @EventHandler
    public void onPlayerArmorChange(PlayerArmorChangeEvent event) {
        super.updateCustomEffectsOnArmorChange(event, PotionEffectType.HEALTH_BOOST);
    }

}
