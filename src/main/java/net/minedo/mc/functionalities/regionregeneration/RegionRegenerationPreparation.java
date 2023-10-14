package net.minedo.mc.functionalities.regionregeneration;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import net.minedo.mc.Minedo;
import net.minedo.mc.constants.common.Common;
import net.minedo.mc.constants.feedbacksound.FeedbackSound;
import net.minedo.mc.models.region.Region;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Launch living entities above when region is about to be built.
 */
public class RegionRegenerationPreparation extends BukkitRunnable {

    private final Chunk chunk;
    private final Region region;
    private final HashMap<String, Integer> restoringChunks;
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Initialize preparation class.
     *
     * @param chunk           chunk
     * @param region          region
     * @param restoringChunks chunks that are restoring
     */
    public RegionRegenerationPreparation(
            @NotNull Chunk chunk, @NotNull Region region, @NotNull HashMap<String, Integer> restoringChunks
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
    private boolean isLivingEntityWithinLaunchingGround(@NotNull LivingEntity livingEntity) {
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
                FeedbackSound feedbackSound = FeedbackSound.REGION_LAUNCH_ENTITIES_ABOVE;

                this.region.worldType().playSound(
                        livingEntity.getLocation(), feedbackSound.getSound(),
                        feedbackSound.getVolume(), feedbackSound.getPitch()
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

    /**
     * Get destroyed blocks percentage.
     *
     * @param world     world
     * @param clipboard clipboard
     * @return destroyed blocks percentage
     */
    private double getDestroyedBlocksPercentage(World world, Clipboard clipboard) {
        BlockVector3 minimumPoint = clipboard.getMinimumPoint();
        BlockVector3 maximumPoint = clipboard.getMaximumPoint();
        int totalBlocks = 0;
        int totalChangedBlocks = 0;

        for (int x = minimumPoint.getX(); x <= maximumPoint.getX(); x++) {
            for (int z = minimumPoint.getZ(); z <= maximumPoint.getZ(); z++) {
                for (int y = minimumPoint.getY(); y <= maximumPoint.getY(); y++) {
                    BlockState blockAtSchematic = clipboard.getBlock(BlockVector3.at(x, y, z));
                    BlockData blockDataAtSchematic = BukkitAdapter.adapt(blockAtSchematic);
                    Block blockAtWorld = world.getBlockAt(x, y, z);

                    if (blockDataAtSchematic.getMaterial() != Material.AIR) {
                        if (blockDataAtSchematic.getMaterial() != blockAtWorld.getType()) {
                            totalChangedBlocks++;
                        }

                        totalBlocks++;
                    }
                }
            }
        }

        if (totalBlocks == 0) {
            return 0;
        } else {
            return (double) totalChangedBlocks / totalBlocks;
        }
    }

    /**
     * Get whether region destroyed to a certain threshold.
     *
     * @param chunk chunk
     * @return whether region destroyed to a certain threshold
     */
    private boolean getWhetherRegionDestroyedByThreshold(@NotNull Chunk chunk) {
        File schematicFile = RegionFileUtils.getSchematicFileForBuilding(this.region, chunk);

        if (schematicFile == null) {
            throw new IllegalStateException("Schematic file is not supposed to be null.");
        }

        double totalModifiedPercentage;
        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(schematicFile);

        try (ClipboardReader clipboardReader = Objects
                .requireNonNull(clipboardFormat)
                .getReader(new FileInputStream(schematicFile))
        ) {
            Clipboard clipboard = clipboardReader.read();
            totalModifiedPercentage = this.getDestroyedBlocksPercentage(this.region.worldType(), clipboard);
        } catch (Exception exception) {
            this.logger.severe(String.format(
                    "Unable to calculate destruction level for chunk (%d, %d) in %s region.",
                    chunk.getX(),
                    chunk.getZ(),
                    this.region.name())
            );
            return false;
        }

        return totalModifiedPercentage >= this.region.modifiedThreshold();
    }

    @Override
    public void run() {
        // Only continue with regeneration if the chunk is modified to a certain threshold.
        boolean isToBeRestored = this.getWhetherRegionDestroyedByThreshold(this.chunk);
        if (!isToBeRestored) {
            return;
        }

        this.launchLivingEntitiesAbove();

        // Run a scheduler to build region after one second of players being launched.
        RegionRegenerationBuilder builder = new RegionRegenerationBuilder(
                this.chunk, this.region, this.restoringChunks
        );

        long DELAY = 1;
        builder.runTaskLater(Minedo.getInstance(), DELAY * (int) Common.TICK_PER_SECOND.getValue());
    }

}
