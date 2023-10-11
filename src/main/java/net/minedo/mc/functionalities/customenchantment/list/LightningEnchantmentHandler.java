package net.minedo.mc.functionalities.customenchantment.list;

import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.CombatEvent;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantment;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentHandler;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentWrapper;
import net.minedo.mc.functionalities.skills.SkillUtils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

/**
 * Spawn lightning upon hit.
 */
public class LightningEnchantmentHandler extends CustomEnchantmentHandler {

    private final HashMap<UUID, Integer> playerSkillPoints;

    /**
     * Initialize lighting enchantment handler.
     */
    public LightningEnchantmentHandler(@NotNull HashMap<UUID, Integer> playerSkillPoints) {
        super(CustomEnchantmentType.LIGHTNING);
        this.playerSkillPoints = playerSkillPoints;
    }

    /**
     * Strike lightning in one line.
     *
     * @param world         world
     * @param startLocation start location
     * @param strikeCount   strike count
     */
    private void strikeLightningInOneLine(World world, Location startLocation, int strikeCount) {
        Location originLocation = startLocation.clone();

        for (int i = 0; i < strikeCount; i++) {
            Vector direction = startLocation.getDirection().clone().multiply(3);

            if (i == 0) {
                startLocation.add(direction);
            } else {
                startLocation.add(direction).add(Vector.getRandom());
            }

            Location strikeLocation = startLocation.clone();

            if (i == 0) {
                if (strikeLocation.getBlockY() < world.getHighestBlockYAt(strikeLocation)) {
                    world.playSound(originLocation, Sound.ENTITY_GUARDIAN_ATTACK, 1, 1);
                    break;
                }
            }

            strikeLocation.setY(world.getHighestBlockYAt(strikeLocation));
            world.strikeLightning(strikeLocation);
        }
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

        LivingEntity defendingEntity = combatEvent.defendingEntity();

        defendingEntity
                .getWorld()
                .strikeLightning(defendingEntity.getLocation());
    }

    @Override
    @EventHandler
    public void onInteract(@NotNull PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = super.isInteractValid(event);

        if (item == null) {
            return;
        }

        Optional<CustomEnchantment> customEnchantmentOptional = CustomEnchantmentWrapper
                .getCustomEnchantment(item, this.getCustomEnchantmentType());

        if (customEnchantmentOptional.isEmpty()) {
            return;
        }

        if (!SkillUtils.canSkill(player, this.playerSkillPoints)) {
            return;
        }

        this.strikeLightningInOneLine(player.getWorld(), player.getLocation(), 5);
    }

}
