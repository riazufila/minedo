package net.minedo.mc.functionalities.customenchantment.list;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

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
    public void onHit(@NotNull EntityDamageByEntityEvent event) {
        super.triggerCustomEffectsOnHit(event, PotionEffectType.FAST_DIGGING, true);
    }

    @Override
    @EventHandler
    public void onPlayerArmorChange(@NotNull PlayerArmorChangeEvent event) {
        super.updateCustomEffectsOnArmorChange(event, PotionEffectType.FAST_DIGGING);
    }

}
