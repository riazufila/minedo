package net.minedo.mc.functionalities.customenchantment.list;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentHandler;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * Gives absorption.
 */
public class AbsorptionEnchantmentHandler extends CustomEnchantmentHandler implements Listener {

    /**
     * Initialize absorption enchantment handler.
     */
    public AbsorptionEnchantmentHandler() {
        super(CustomEnchantmentType.ABSORPTION);
    }

    @EventHandler
    public void onHit(@NotNull EntityDamageByEntityEvent event) {
        Entity defender = event.getEntity();
        Entity attacker = event.getDamager();
        super.triggerPotionEffectsOnHit(defender, attacker, PotionEffectType.ABSORPTION, true);
    }

    @EventHandler
    public void onPlayerArmorChange(@NotNull PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();
        super.updatePotionEffectsOnArmorChange(player, PotionEffectType.ABSORPTION);
    }

}
