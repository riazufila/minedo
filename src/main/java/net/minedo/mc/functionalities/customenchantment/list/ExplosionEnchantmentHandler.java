package net.minedo.mc.functionalities.customenchantment.list;

import net.minedo.mc.Minedo;
import net.minedo.mc.constants.common.Common;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.common.utils.ParticleUtils;
import net.minedo.mc.functionalities.customenchantment.CombatEvent;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantment;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentHandler;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentWrapper;
import net.minedo.mc.functionalities.skills.SkillUtils;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

/**
 * Grants explosion.
 */
public class ExplosionEnchantmentHandler extends CustomEnchantmentHandler {

    private final HashMap<UUID, Integer> playerSkillPoints;
    private final HashMap<UUID, Integer> playersExploding = new HashMap<>();

    /**
     * Initialize explosion enchantment handler.
     */
    public ExplosionEnchantmentHandler(HashMap<UUID, Integer> playerSkillPoints) {
        super(CustomEnchantmentType.EXPLOSION);
        this.playerSkillPoints = playerSkillPoints;
    }

    /**
     * Runs explosion runnable.
     *
     * @param player player
     * @param delay  delay
     * @return explosion runnable task ID
     */
    private int explosionRunnable(@NotNull Player player, long delay) {
        return new BukkitRunnable() {

            int countDown = 1;

            @Override
            public void run() {
                float EXPLOSION_POWER = 10.0f;
                Location location = player.getLocation().getBlock().getRelative(BlockFace.UP).getLocation();

                if (player.isOnline() && countDown > delay) {
                    player.getWorld().createExplosion(location, EXPLOSION_POWER);
                    playersExploding.remove(player.getUniqueId());

                    this.cancel();
                }

                if (player.isOnline()) {
                    countDown++;
                }
            }

        }.runTaskTimer(
                Minedo.getInstance(),
                0,
                (int) Common.TICK_PER_SECOND.getValue()
        ).getTaskId();
    }

    /**
     * Runs explosion particles runnable.
     *
     * @param player player
     * @param delay  delay
     */
    private void explosionParticlesRunnable(@NotNull Player player, long delay) {
        final int DENSITY = 2;
        new BukkitRunnable() {

            int countDown = 1;

            @Override
            public void run() {
                if (countDown >= delay * DENSITY) {
                    this.cancel();
                }

                if (player.isOnline()) {
                    ParticleUtils.spawnParticleOnEntity(
                            player,
                            Particle.REDSTONE,
                            3,
                            1,
                            new Particle.DustOptions(Color.GRAY, 2f),
                            0.3
                    );

                    countDown++;
                }
            }

        }.runTaskTimer(Minedo.getInstance(), 0, (int) Common.TICK_PER_SECOND.getValue() / DENSITY);
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

        final float EXPLOSION_POWER = 1.0f;
        defendingEntity
                .getWorld()
                .createExplosion(defendingEntity.getLocation(), EXPLOSION_POWER);
    }

    @Override
    @EventHandler
    public void onInteract(@NotNull PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID playerUuid = player.getUniqueId();

        if (playersExploding.get(playerUuid) != null) {
            return;
        }

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

        final long DELAY = 3;
        final int POTION_AMPLIFIER = 9;
        int explosionTaskId = this.explosionRunnable(player, DELAY);
        this.explosionParticlesRunnable(player, DELAY);

        PotionEffect potionEffect = new PotionEffect(
                PotionEffectType.SLOW,
                (int) (DELAY * (int) Common.TICK_PER_SECOND.getValue()),
                POTION_AMPLIFIER
        );

        player.addPotionEffect(potionEffect);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 1, 1);
        playersExploding.put(playerUuid, explosionTaskId);
    }

    @EventHandler
    public void onPlayerDeath(@NotNull PlayerDeathEvent event) {
        Player player = event.getPlayer();
        UUID playerUuid = player.getUniqueId();
        Integer explosionTaskId = playersExploding.get(playerUuid);

        if (explosionTaskId == null) {
            return;
        }

        playersExploding.remove(playerUuid);
        Bukkit.getScheduler().cancelTask(explosionTaskId);
    }

}
