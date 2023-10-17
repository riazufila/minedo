package net.minedo.mc.functionalities.customenchantment.list;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * Grants haste.
 */
public class HasteEnchantmentHandler extends CustomEnchantmentHandler implements Listener {

    /**
     * Initialize haste enchantment handler.
     */
    public HasteEnchantmentHandler() {
        super(CustomEnchantmentType.HASTE);
    }

    @EventHandler
    public void onPlayerArmorChange(@NotNull PlayerArmorChangeEvent event) {
        super.updatePotionEffectsOnArmorChange(event, PotionEffectType.FAST_DIGGING);
    }

}
