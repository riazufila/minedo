package net.minedo.mc.functionalities.customenchantment;

import net.minedo.mc.constants.common.Common;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public abstract class CustomEnchantmentHandler extends SimpleCustomEnchantment implements Listener {

    public CustomEnchantmentHandler(CustomEnchantmentType customEnchantmentType) {
        super(customEnchantmentType);
    }

    public CombatEvent isOnHitValid(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity defendingEntity)) {
            return null;
        }

        if (!(event.getDamager() instanceof LivingEntity attackingEntity)) {
            return null;
        }


        if (attackingEntity.getEquipment() == null) {
            return null;
        }

        ItemStack itemAtHand = attackingEntity.getEquipment().getItemInMainHand();

        if (itemAtHand.isEmpty()) {
            return null;
        }

        if (attackingEntity instanceof Player attackingPlayer) {
            if (attackingPlayer.getAttackCooldown() != (float) Common.ATTACK_COOL_DOWN.getValue()) {
                return null;
            }
        }

        return new CombatEvent(itemAtHand, attackingEntity, defendingEntity);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        // Override and add custom effects if needed.
    }

    @EventHandler
    public void onDamaged(EntityDamageEvent event) {
        // Override and add custom effects if needed.
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        // Override and add custom effects if needed.
    }

}
