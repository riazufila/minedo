package net.minedo.mc.functionalities.customenchantment.list;

import net.minedo.mc.Minedo;
import net.minedo.mc.constants.common.Common;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.common.utils.ParticleUtils;
import net.minedo.mc.functionalities.common.utils.PlayerUtils;
import net.minedo.mc.functionalities.customenchantment.CombatEvent;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantment;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentHandler;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

/**
 * Grants explosion.
 */
public class ExplosionEnchantmentHandler extends CustomEnchantmentHandler {

    private final HashMap<UUID, Integer> playersExploding = new HashMap<>();

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
        UUID playerUuid = player.getUniqueId();

        if (playersExploding.get(playerUuid) != null) {
            return;
        }

        ItemStack item = player.getEquipment().getItemInOffHand();

        if (!super.isInteractValid(event, item)) {
            return;
        }

        Optional<CustomEnchantment> customEnchantmentOptional = CustomEnchantmentWrapper
                .getCustomEnchantment(item, this.getCustomEnchantmentType());

        if (customEnchantmentOptional.isEmpty()) {
            return;
        }

        long DELAY = 3;
        int taskId = new BukkitRunnable() {

            @Override
            public void run() {
                float EXPLOSION_POWER = 10.0f;
                Location location = player.getLocation().getBlock().getRelative(BlockFace.UP).getLocation();

                if (player.isOnline()) {
                    player.getWorld().createExplosion(location, EXPLOSION_POWER);
                }

                playersExploding.remove(playerUuid);
            }

        }.runTaskLater(Minedo.getInstance(), DELAY * (int) Common.TICK_PER_SECOND.getValue()).getTaskId();

        ParticleUtils.spawnParticleOnEntity(
                player,
                Particle.REDSTONE,
                5,
                1,
                new Particle.DustOptions(Color.GRAY, 1.5f),
                0.2
        );
        playersExploding.put(playerUuid, taskId);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        UUID playerUuid = event.getPlayer().getUniqueId();
        Integer taskId = playersExploding.get(playerUuid);

        if (taskId == null) {
            return;
        }

        playersExploding.remove(playerUuid);
        Bukkit.getScheduler().cancelTask(taskId);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!PlayerUtils.isPlayerMoving(event)) {
            return;
        }

        UUID playerUuid = event.getPlayer().getUniqueId();
        Integer taskId = playersExploding.get(playerUuid);

        if (taskId == null) {
            return;
        }

        if (PlayerUtils.isPlayerFalling(event)) {
            return;
        }

        event.setCancelled(true);
    }

}
