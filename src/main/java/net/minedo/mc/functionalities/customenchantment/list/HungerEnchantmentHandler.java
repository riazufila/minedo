package net.minedo.mc.functionalities.customenchantment.list;

import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class HungerEnchantmentHandler extends CustomEnchantmentHandler {

    public HungerEnchantmentHandler() {
        super(CustomEnchantmentType.HUNGER);
    }

    @Override
    @EventHandler
    public void onHit(@NotNull EntityDamageByEntityEvent event) {
        super.triggerCustomEffectsOnHit(event, PotionEffectType.HUNGER, true);
    }

}
