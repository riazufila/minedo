package net.minedo.mc.functionalities.customenchantment.list;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * Grants jump.
 */
public class JumpEnchantmentHandler extends CustomEnchantmentHandler implements Listener {

    /**
     * Initialize jump enchantment handler.
     */
    public JumpEnchantmentHandler() {
        super(CustomEnchantmentType.JUMP);
    }

    @EventHandler
    public void onPlayerArmorChange(@NotNull PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();
        super.updatePotionEffectsOnArmorChange(player, PotionEffectType.JUMP);
    }

}
