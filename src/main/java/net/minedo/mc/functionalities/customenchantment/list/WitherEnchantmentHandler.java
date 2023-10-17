package net.minedo.mc.functionalities.customenchantment.list;

import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.customevents.PlayerNonBlockInteractEvent;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentHandler;
import net.minedo.mc.functionalities.utils.ShapeUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Inflicts wither.
 */
public class WitherEnchantmentHandler extends CustomEnchantmentHandler implements Listener {

    private final List<UUID> launchedProjectiles = new ArrayList<>();
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
        super.triggerPotionEffectsOnHit(event, PotionEffectType.WITHER, true);
    }

    @EventHandler
    public void onInteract(@NotNull PlayerNonBlockInteractEvent event) {
        Player player = event.getPlayer();
        EquipmentSlot equipmentSlot = event.getHand();
        ItemStack itemUsed = event.getItem();

        boolean isAbleToSkill = super.isPlayerAbleToSkill(player, equipmentSlot, itemUsed, this.playerSkillPoints);
        if (!isAbleToSkill) {
            return;
        }

        Vector velocity = player.getLocation().getDirection().multiply(0.5);
        Projectile projectile = player.launchProjectile(WitherSkull.class, velocity);
        this.launchedProjectiles.add(projectile.getUniqueId());
    }

    @EventHandler
    public void onProjectileHit(@NotNull ProjectileHitEvent event) {
        UUID projectileUuid = event.getEntity().getUniqueId();

        if (!this.launchedProjectiles.contains(projectileUuid)) {
            return;
        }

        Entity hitEntity = event.getHitEntity();
        Block hitBlock = event.getHitBlock();

        if (!(hitEntity != null || hitBlock != null)) {
            return;
        }

        final int RADIUS = 5;
        Location hitLocation = hitEntity != null ? hitEntity.getLocation() : hitBlock.getLocation();
        World world = hitLocation.getWorld();

        for (int radius = 1; radius <= RADIUS; radius++) {
            for (Location spherePoint : ShapeUtils.getSphere(hitLocation, radius)) {
                Material currentLocationBlockType = spherePoint.getBlock().getType();

                if (currentLocationBlockType == Material.AIR) {
                    continue;
                }
                world.getBlockAt(spherePoint).setType(Material.SOUL_SAND);
            }
        }

        this.launchedProjectiles.remove(projectileUuid);
    }

}
