package net.minedo.mc.functionalities.customenchantment.list;

import net.minedo.mc.Minedo;
import net.minedo.mc.constants.common.Common;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.constants.feedbacksound.FeedbackSound;
import net.minedo.mc.customevents.PlayerNonBlockInteractEvent;
import net.minedo.mc.functionalities.customenchantment.CombatEvent;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentHandler;
import net.minedo.mc.functionalities.utils.ParticleUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

/**
 * Grants explosion.
 */
public class ExplosionEnchantmentHandler extends CustomEnchantmentHandler implements Listener {

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
                    location.createExplosion(EXPLOSION_POWER);
                    playersExploding.remove(player.getUniqueId());

                    this.cancel();
                    return;
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
                    return;
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

    @EventHandler
    public void onHit(@NotNull EntityDamageByEntityEvent event) {
        CombatEvent combatEvent = super.isAbleToInflictCustomEnchantmentOnHit(event);

        if (combatEvent == null || combatEvent.getCustomEnchantment() == null) {
            return;
        }

        LivingEntity defendingEntity = combatEvent.getDefendingEntity();
        final float EXPLOSION_POWER = 1.0f;
        defendingEntity
                .getLocation()
                .createExplosion(EXPLOSION_POWER);
    }

    @EventHandler
    public void onInteract(@NotNull PlayerNonBlockInteractEvent event) {
        Player player = event.getPlayer();
        EquipmentSlot equipmentSlot = event.getHand();
        ItemStack itemUsed = event.getItem();
        UUID playerUuid = player.getUniqueId();

        if (playersExploding.get(playerUuid) != null) {
            return;
        }

        boolean isAbleToSkill = super.isPlayerAbleToSkill(player, equipmentSlot, itemUsed, this.playerSkillPoints);
        if (!isAbleToSkill) {
            return;
        }

        final long DELAY = 3;
        final int POTION_AMPLIFIER = 9;
        int explosionTaskId = this.explosionRunnable(player, DELAY);
        this.explosionParticlesRunnable(player, DELAY);
        FeedbackSound feedbackSound = FeedbackSound.EXPLOSION_SKILL;

        PotionEffect potionEffect = new PotionEffect(
                PotionEffectType.SLOW,
                (int) (DELAY * (int) Common.TICK_PER_SECOND.getValue()),
                POTION_AMPLIFIER
        );

        player.addPotionEffect(potionEffect);
        player.getWorld().playSound(player.getLocation(), feedbackSound.getSound(),
                feedbackSound.getVolume(), feedbackSound.getPitch());
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
