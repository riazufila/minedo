package net.minedo.mc.functionalities.customenchantment.list;

import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.CombatEvent;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantment;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentHandler;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentWrapper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Grants explosion.
 */
public class ExplosionEnchantmentHandler extends CustomEnchantmentHandler {

    /**
     * Initialize explosion enchantment handler.
     */
    public ExplosionEnchantmentHandler() {
        super(CustomEnchantmentType.EXPLOSION);
    }

    @Override
    @EventHandler
    public void onHit(@NotNull EntityDamageByEntityEvent event) {
        CombatEvent combatEvent = super.isOnHitValid(event);

        if (combatEvent == null) {
            return;
        }

        Optional<CustomEnchantment> customEnchantmentOptional = CustomEnchantmentWrapper
                .getCustomEnchantment(combatEvent.item(), this.getCustomEnchantmentType());


        if (customEnchantmentOptional.isEmpty()) {
            return;
        }

        LivingEntity defendingEntity = combatEvent.defendingEntity();

        float EXPLOSION_POWER = 1.0f;
        defendingEntity
                .getWorld()
                .createExplosion(defendingEntity.getLocation(), EXPLOSION_POWER);
    }

    @Override
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getEquipment().getItemInOffHand();

        if (item.isEmpty()) {
            item = player.getEquipment().getItemInMainHand();
        }


        if (!super.isInteractValid(event.getAction(), item)) {
            return;
        }

        Optional<CustomEnchantment> customEnchantmentOptional = CustomEnchantmentWrapper
                .getCustomEnchantment(item, this.getCustomEnchantmentType());

        if (customEnchantmentOptional.isEmpty()) {
            return;
        }

        float EXPLOSION_POWER = 10.0f;
        player.getWorld().createExplosion(player.getLocation(), EXPLOSION_POWER);
    }

}
