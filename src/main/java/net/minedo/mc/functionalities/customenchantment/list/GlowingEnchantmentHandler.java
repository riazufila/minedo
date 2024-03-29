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
 * Gives glowing effect.
 */
public class GlowingEnchantmentHandler extends CustomEnchantmentHandler implements Listener {

    /**
     * Initialize glowing enchantment handler.
     */
    public GlowingEnchantmentHandler() {
        super(CustomEnchantmentType.GLOWING);
    }

    @EventHandler
    public void onHit(@NotNull EntityDamageByEntityEvent event) {
        Entity defender = event.getEntity();
        Entity attacker = event.getDamager();
        super.triggerPotionEffectsOnHit(defender, attacker, PotionEffectType.GLOWING, false);
    }

    @EventHandler
    public void onPlayerArmorChange(@NotNull PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();
        super.updatePotionEffectsOnArmorChange(player, PotionEffectType.GLOWING);
    }

}
