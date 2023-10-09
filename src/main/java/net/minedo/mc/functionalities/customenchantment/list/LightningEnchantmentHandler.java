package net.minedo.mc.functionalities.customenchantment.list;

import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.CombatEvent;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantment;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentHandler;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentWrapper;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Spawn lightning upon hit.
 */
public class LightningEnchantmentHandler extends CustomEnchantmentHandler {

    /**
     * Initialize lighting enchantment handler.
     */
    public LightningEnchantmentHandler() {
        super(CustomEnchantmentType.LIGHTNING);
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

        Location location = event.getEntity().getLocation();
        World world = combatEvent.defendingEntity().getWorld();

        if (location.getBlockY() < world.getHighestBlockYAt(location)) {
            return;
        }

        combatEvent
                .defendingEntity()
                .getWorld()
                .strikeLightning(event.getEntity().getLocation());
    }

}
