package net.minedo.mc.regionregeneration;

import com.sk89q.worldedit.WorldEdit;
import net.minedo.mc.Minedo;
import net.minedo.mc.models.region.Region;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.logging.Logger;

public class RegionRegenerationScheduler extends BukkitRunnable {

    private final Chunk chunk;
    private final Region region;
    private final World world;
    private final WorldEdit worldEdit;
    private final Minedo pluginInstance;
    private final Logger logger;
    private final Map<String, Integer> restoringChunks;

    public RegionRegenerationScheduler(
            Chunk chunk, Region region, World world,
            WorldEdit worldEdit, Logger logger,
            Minedo pluginInstance, Map<String, Integer> restoringChunks
    ) {
        this.chunk = chunk;
        this.region = region;
        this.world = world;
        this.worldEdit = worldEdit;
        this.pluginInstance = pluginInstance;
        this.logger = logger;
        this.restoringChunks = restoringChunks;
    }

    private boolean isPlayerWithinLaunchingGround(Player player) {
        Location location = player.getLocation();
        int highestBlockAtY = this.world.getHighestBlockYAt(location.getBlockX(), location.getBlockZ());
        int LAUNCHING_GROUND_MAX_HEIGHT = 5;

        return location.getBlockY() - highestBlockAtY <= LAUNCHING_GROUND_MAX_HEIGHT;
    }

    private void launchPlayersAbove() {
        Entity[] entities = this.chunk.getEntities();

        for (Entity entity : entities) {
            if (entity instanceof Player player && this.isPlayerWithinLaunchingGround(player)) {
                this.world.playSound(player.getLocation(), Sound.BLOCK_AZALEA_LEAVES_STEP, 1, 1);
                player.setVelocity(player.getLocation().getDirection().multiply(2).setX(0).setZ(0).setY(2));

                PotionEffect existingPotionEffect = player.getPotionEffect(PotionEffectType.SLOW_FALLING);
                int POTION_EFFECT_DURATION = 100;

                if (existingPotionEffect != null) {
                    int existingDuration = existingPotionEffect.getDuration();

                    player.addPotionEffect(new PotionEffect(
                            PotionEffectType.SLOW_FALLING,
                            existingDuration + POTION_EFFECT_DURATION, 0
                    ));
                } else {
                    player.addPotionEffect(new PotionEffect(
                            PotionEffectType.SLOW_FALLING, POTION_EFFECT_DURATION, 0
                    ));
                }
            }
        }
    }

    @Override
    public void run() {
        this.launchPlayersAbove();

        // Run a scheduler to build region after one second of players being launched.
        RegionRegenerationBuilder builder = new RegionRegenerationBuilder(
                this.chunk, this.region, this.world, this.worldEdit, this.logger, this.restoringChunks
        );

        builder.runTaskLater(this.pluginInstance, 20);
    }

}
