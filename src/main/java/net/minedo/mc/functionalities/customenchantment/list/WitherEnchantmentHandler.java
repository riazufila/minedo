package net.minedo.mc.functionalities.customenchantment.list;

import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.constants.feedbacksound.FeedbackSound;
import net.minedo.mc.customevents.PlayerNonBlockInteractEvent;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantment;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentHandler;
import net.minedo.mc.functionalities.utils.ShapeUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

/**
 * Inflicts wither.
 */
public class WitherEnchantmentHandler extends CustomEnchantmentHandler implements Listener {

    private final HashMap<UUID, CustomEnchantment> launchedProjectiles = new HashMap<>();
    private final HashMap<UUID, Integer> playerSkillPoints;

    /**
     * Initialize wither enchantment handler.
     */
    public WitherEnchantmentHandler(@NotNull HashMap<UUID, Integer> playerSkillPoints) {
        super(CustomEnchantmentType.WITHER);
        this.playerSkillPoints = playerSkillPoints;
    }

    @EventHandler
    public void onHit(@NotNull EntityDamageByEntityEvent event) {
        Entity defender = event.getEntity();
        Entity attacker = event.getDamager();
        super.triggerPotionEffectsOnHit(defender, attacker, PotionEffectType.WITHER, true);
    }

    @EventHandler
    public void onInteract(@NotNull PlayerNonBlockInteractEvent event) {
        Player player = event.getPlayer();
        EquipmentSlot equipmentSlot = event.getHand();
        ItemStack itemUsed = event.getItem();

        CustomEnchantment customEnchantment = super.getCustomEnchantmentOnSKill(
                player, equipmentSlot, itemUsed, this.playerSkillPoints
        );

        if (customEnchantment == null) {
            return;
        }

        Vector velocity = player.getLocation().getDirection().multiply(0.5);
        Projectile projectile = player.launchProjectile(WitherSkull.class, velocity);
        FeedbackSound feedbackSound = FeedbackSound.WITHER_SKILL;

        player.getWorld().playSound(
                player, feedbackSound.getSound(), feedbackSound.getVolume(), feedbackSound.getPitch()
        );

        this.launchedProjectiles.put(projectile.getUniqueId(), customEnchantment);
    }

    @EventHandler
    public void onProjectileHit(@NotNull ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        UUID projectileUuid = projectile.getUniqueId();

        if (!this.launchedProjectiles.containsKey(projectileUuid)) {
            return;
        }

        CustomEnchantment customEnchantment = this.launchedProjectiles.get(projectileUuid);
        Entity hitEntity = event.getHitEntity();
        Block hitBlock = event.getHitBlock();

        if (!(hitEntity != null || hitBlock != null)) {
            return;
        }

        final int RADIUS = 5;
        Location hitLocation = hitEntity != null ? hitEntity.getLocation() : hitBlock.getLocation();
        World world = hitLocation.getWorld();

        if (hitEntity instanceof LivingEntity livingEntity) {
            PotionEffect potionEffect = super.getPotionEffectOnHit(
                    customEnchantment, PotionEffectType.WITHER, true
            );

            livingEntity.addPotionEffect(potionEffect);
        }

        for (int radius = 1; radius <= RADIUS; radius++) {
            for (Location spherePoint : ShapeUtils.getSphere(hitLocation, radius)) {
                Material currentLocationBlockType = spherePoint.getBlock().getType();
                int coordinateBlockX = spherePoint.getBlockX();
                int coordinateBlockY = spherePoint.getBlockY();
                int coordinateBlockZ = spherePoint.getBlockZ();
                int coordinateHighestBlockY = world.getHighestBlockYAt(
                        coordinateBlockX, coordinateBlockZ
                );

                if (currentLocationBlockType == Material.AIR) {
                    continue;
                }

                if (coordinateBlockY == coordinateHighestBlockY) {
                    world.spawnParticle(
                            Particle.SOUL,
                            coordinateBlockX,
                            coordinateBlockY,
                            coordinateBlockZ,
                            1,
                            0.3,
                            1.3,
                            0.3,
                            0)
                    ;
                }


                world.getBlockAt(spherePoint).setType(Material.SOUL_SAND);
            }
        }

        this.launchedProjectiles.remove(projectileUuid);
    }

}
