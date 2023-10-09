package net.minedo.mc.functionalities.regionregeneration;

import net.minedo.mc.Minedo;
import net.minedo.mc.constants.common.Common;
import net.minedo.mc.models.region.Region;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

/**
 * Launch living entities above when region is about to be built.
 */
public class RegionRegenerationLauncher extends BukkitRunnable {

    private final Chunk chunk;
    private final Region region;
    private final HashMap<String, Integer> restoringChunks;

    /**
     * Initialize launcher.
     *
     * @param chunk           chunk
     * @param region          region
     * @param restoringChunks chunks that are restoring
     */
    public RegionRegenerationLauncher(
            Chunk chunk, Region region, HashMap<String, Integer> restoringChunks
    ) {
        this.chunk = chunk;
        this.region = region;
        this.restoringChunks = restoringChunks;
    }

    /**
     * Get whether living entity is within launching ground.
     *
     * @param livingEntity living entity
     * @return whether living entity is within launching ground
     */
    private boolean isLivingEntityWithinLaunchingGround(LivingEntity livingEntity) {
        Location location = livingEntity.getLocation();
        int LAUNCHING_GROUND_MAX_HEIGHT = 5;

        int highestBlockAtY = this.region.worldType().getHighestBlockYAt(
                location.getBlockX(), location.getBlockZ()
        );

        return location.getBlockY() - highestBlockAtY <= LAUNCHING_GROUND_MAX_HEIGHT;
    }

    /**
     * Launch living entities above.
     */
    private void launchLivingEntitiesAbove() {
        Entity[] entities = this.chunk.getEntities();

        for (Entity entity : entities) {
            if (entity instanceof LivingEntity livingEntity
                    && this.isLivingEntityWithinLaunchingGround(livingEntity)
            ) {
                this.region.worldType().playSound(
                        livingEntity.getLocation(), Sound.BLOCK_AZALEA_LEAVES_STEP, 1, 1
                );

                livingEntity.setVelocity(
                        livingEntity.getLocation().getDirection().multiply(2).setX(0).setZ(0).setY(2)
                );

                PotionEffect existingPotionEffect = livingEntity.getPotionEffect(PotionEffectType.SLOW_FALLING);
                int POTION_EFFECT_DURATION = 100;

                if (existingPotionEffect != null) {
                    int existingDuration = existingPotionEffect.getDuration();

                    livingEntity.addPotionEffect(new PotionEffect(
                            PotionEffectType.SLOW_FALLING,
                            existingDuration + POTION_EFFECT_DURATION, 0
                    ));
                } else {
                    livingEntity.addPotionEffect(new PotionEffect(
                            PotionEffectType.SLOW_FALLING, POTION_EFFECT_DURATION, 0
                    ));
                }
            }
        }
    }

    @Override
    public void run() {
        this.launchLivingEntitiesAbove();

        // Run a scheduler to build region after one second of players being launched.
        RegionRegenerationBuilder builder = new RegionRegenerationBuilder(
                this.chunk, this.region, this.restoringChunks
        );

        long DELAY = 1;
        builder.runTaskLater(Minedo.getInstance(), DELAY * (int) Common.TICK_PER_SECOND.getValue());
    }

}
