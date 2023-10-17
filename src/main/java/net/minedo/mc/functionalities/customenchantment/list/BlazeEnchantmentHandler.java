package net.minedo.mc.functionalities.customenchantment.list;

import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.customevents.PlayerNonBlockInteractEvent;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentHandler;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Spawn fireball projectile.
 */
public class BlazeEnchantmentHandler extends CustomEnchantmentHandler implements Listener {

    private final List<UUID> launchedProjectiles = new ArrayList<>();
    private final HashMap<UUID, Integer> playerSkillPoints;

    /**
     * Initialize blaze enchantment handler.
     */
    public BlazeEnchantmentHandler(@NotNull HashMap<UUID, Integer> playerSkillPoints) {
        super(CustomEnchantmentType.BLAZE);
        this.playerSkillPoints = playerSkillPoints;
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
        Projectile projectile = player.launchProjectile(Fireball.class, velocity);
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

        final float EXPLOSION_POWER = 1.9f;
        Location hitLocation = hitEntity != null ? hitEntity.getLocation() : hitBlock.getLocation();
        hitLocation.createExplosion(EXPLOSION_POWER);

        this.launchedProjectiles.remove(projectileUuid);
    }

}
