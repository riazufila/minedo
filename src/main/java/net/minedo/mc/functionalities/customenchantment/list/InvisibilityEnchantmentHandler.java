package net.minedo.mc.functionalities.customenchantment.list;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * Grants invisibility.
 */
public class InvisibilityEnchantmentHandler extends CustomEnchantmentHandler implements Listener {

    /**
     * Initialize invisibility enchantment handler.
     */
    public InvisibilityEnchantmentHandler() {
        super(CustomEnchantmentType.INVISIBILITY);
    }

    @EventHandler
    public void onPlayerArmorChange(@NotNull PlayerArmorChangeEvent event) {
        super.updatePotionEffectsOnArmorChange(event, PotionEffectType.INVISIBILITY);
    }

}
