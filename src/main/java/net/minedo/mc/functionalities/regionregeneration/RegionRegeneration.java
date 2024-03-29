package net.minedo.mc.functionalities.regionregeneration;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import io.papermc.paper.event.entity.EntityMoveEvent;
import net.minedo.mc.Minedo;
import net.minedo.mc.constants.common.Common;
import net.minedo.mc.functionalities.utils.ParticleUtils;
import net.minedo.mc.interfaces.chunkprocessor.ChunkProcessor;
import net.minedo.mc.models.region.Region;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * Region chunk regeneration.
 */
public class RegionRegeneration implements Listener {

    private final Region region;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final HashMap<String, Integer> restoringChunks = new HashMap<>();

    /**
     * Initialize region regeneration.
     *
     * @param region region
     */
    public RegionRegeneration(@NotNull Region region) {
        this.region = region;
    }

    /**
     * Iterates through a minimum and maximum location to get the chunks.
     *
     * @param world          world
     * @param chunkProcessor final process to run through after chunks info is attained
     * @param isGetter       whether process is for snapshot getter or setter
     * @return whether process fails or not
     */
    private boolean processChunks(@NotNull World world, @NotNull ChunkProcessor chunkProcessor, boolean isGetter) {
        int CHUNK_SIZE = (int) Common.CHUNK_SIZE.getValue();
        int minX = this.region.minX();
        int maxX = this.region.maxX();
        int minZ = this.region.minZ();
        int maxZ = this.region.maxZ();

        // Iterate through chunks and check if a schematic for each chunk has been created.
        for (
                int chunkX = minX / CHUNK_SIZE;
                maxX >= 0 ? chunkX <= maxX / CHUNK_SIZE : chunkX < maxX / CHUNK_SIZE;
                chunkX++
        ) {
            for (
                    int chunkZ = minZ / CHUNK_SIZE;
                    maxZ > 0 ? chunkZ <= maxZ / CHUNK_SIZE : chunkZ < maxZ / CHUNK_SIZE;
                    chunkZ++
            ) {
                int chunkMinX = chunkX * CHUNK_SIZE;
                int chunkMaxX = chunkMinX + CHUNK_SIZE - 1;
                int chunkMinZ = chunkZ * CHUNK_SIZE;
                int chunkMaxZ = chunkMinZ + CHUNK_SIZE - 1;

                if (chunkMinX < chunkMaxX && chunkMinZ < chunkMaxZ) {
                    Object[] params;

                    if (isGetter) {
                        params = new Object[]{chunkX, chunkZ};
                    } else {
                        params = new Object[]{world, chunkMinX, chunkMaxX, chunkMinZ, chunkMaxZ, chunkX, chunkZ};
                    }

                    if (!chunkProcessor.process(params)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Get whether snapshot exists.
     *
     * @param params params
     * @return whether snapshot exists
     */
    private boolean checkSnapshotExists(@NotNull Object[] params) {
        int chunkX = (int) params[0];
        int chunkZ = (int) params[1];

        File file = RegionFileUtils.getFile(this.region, chunkX, chunkZ);

        if (!file.exists()) {
            this.logger.severe(String.format(
                    "Unable to get %s region snapshot.", this.region.name()
            ));

            return false;
        }

        return true;
    }

    /**
     * Create snapshot.
     *
     * @param params params
     * @return whether snapshot is created
     */
    private boolean createSnapshot(@NotNull Object[] params) {
        World world = (World) params[0];
        int chunkMinX = (int) params[1];
        int chunkMaxX = (int) params[2];
        int chunkMinZ = (int) params[3];
        int chunkMaxZ = (int) params[4];
        int chunkX = (int) params[5];
        int chunkZ = (int) params[6];

        CuboidRegion cuboidRegion = new CuboidRegion(
                BukkitAdapter.adapt(world),
                BlockVector3.at(chunkMinX, world.getMinHeight(), chunkMinZ),
                BlockVector3.at(chunkMaxX, world.getMaxHeight(), chunkMaxZ)
        );

        BlockArrayClipboard blockArrayClipboard = new BlockArrayClipboard(cuboidRegion);
        WorldEdit worldEdit = WorldEdit.getInstance();
        EditSession editSession = worldEdit.newEditSession(BukkitAdapter.adapt(world));

        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                editSession,
                cuboidRegion,
                blockArrayClipboard,
                cuboidRegion.getMinimumPoint()
        );

        try {
            Operations.complete(forwardExtentCopy);
        } catch (WorldEditException e) {
            this.logger.severe(String.format(
                    "Unable to copy %s region snapshot into memory: %s",
                    this.region.name(),
                    e.getMessage()
            ));
            return false;
        }

        File file = RegionFileUtils.getFile(this.region, chunkX, chunkZ);

        try (ClipboardWriter clipboardWriter = BuiltInClipboardFormat.FAST.getWriter(
                new FileOutputStream(file)
        )) {
            clipboardWriter.write(blockArrayClipboard);
        } catch (IOException e) {
            this.logger.severe(String.format(
                    "Unable to write %s region snapshot into disk: %s",
                    this.region.name(),
                    e.getMessage()
            ));
            return false;
        }

        return true;
    }

    /**
     * Get region snapshot.
     *
     * @return whether region has a snapshot
     */
    public boolean getRegionSnapshot() {
        this.logger.info(String.format("Getting %s region snapshot.", this.region.name()));

        return processChunks(this.region.worldType(), this::checkSnapshotExists, true);
    }

    /**
     * Set region snapshot.
     */
    public void setRegionSnapshot() {
        this.logger.info(String.format("Setting %s region snapshot.", this.region.name()));
        processChunks(this.region.worldType(), this::createSnapshot, false);
    }

    /**
     * Regenerate a region's chunk.
     *
     * @param chunk chunk
     */
    public void restoreRegionChunk(@NotNull Chunk chunk) {
        String restoringChunkKey = String.format("(%d,%d)", chunk.getX(), chunk.getZ());

        if (restoringChunks.containsKey(restoringChunkKey)) {
            Bukkit.getScheduler().cancelTask(restoringChunks.get(restoringChunkKey));
        }

        // Run region regeneration scheduler after 30 seconds.
        RegionRegenerationPreparation regionRegenerationPreparation = new RegionRegenerationPreparation(
                chunk, this.region, this.restoringChunks
        );

        long DELAY = 30;
        int restoringTaskId = regionRegenerationPreparation
                .runTaskLater(Minedo.getInstance(), DELAY * (int) Common.TICK_PER_SECOND.getValue())
                .getTaskId();

        // Place task ID and update restoring chunk details.
        restoringChunks.put(restoringChunkKey, restoringTaskId);
    }

    /**
     * Regenerate.
     *
     * @param block block
     */
    private void regenerate(@NotNull Block block) {
        Chunk chunk = block.getChunk();

        // Check if block destroyed is in region.
        if (!this.region.isWithinRegion(block.getLocation())) {
            return;
        }

        // Restore chunk.
        this.restoreRegionChunk(chunk);
    }

    /**
     * Regenerate.
     *
     * @param blocks blocks
     */
    private void regenerate(@NotNull List<Block> blocks) {
        List<Chunk> chunks = new ArrayList<>();

        for (Block block : blocks) {
            Chunk chunk = block.getChunk();

            if (chunks.contains(chunk)) {
                continue;
            }

            // Check if block destroyed is in region.
            if (!this.region.isWithinRegion(block.getLocation())) {
                return;
            }

            // Restore chunk.
            this.restoreRegionChunk(chunk);
            chunks.add(chunk);
        }
    }

    /**
     * Get whether entity is allowed to be in region.
     *
     * @param entity      living entity
     * @param spawnReason spawn reason
     * @return whether entity is allowed to be in region
     */
    private boolean getEntityIsAllowedInRegion(
            @NotNull LivingEntity entity, @NotNull CreatureSpawnEvent.SpawnReason spawnReason
    ) {
        if (!(entity instanceof Monster || entity instanceof Flying)) {
            return true;
        }

        return spawnReason != CreatureSpawnEvent.SpawnReason.NATURAL;
    }

    /**
     * Set entity with potion effects.
     *
     * @param entity entity
     */
    private void setPotionEffectToBannedEntityInRegions(@NotNull LivingEntity entity) {
        PotionEffectType potionEffectType = entity.getCategory() == EntityCategory.UNDEAD
                ? PotionEffectType.HEAL
                : PotionEffectType.HARM;

        PotionEffect potionEffect = new PotionEffect(
                potionEffectType, PotionEffect.INFINITE_DURATION, 0
        );

        ParticleUtils.spawnParticleOnEntity(
                entity,
                Particle.REDSTONE,
                2,
                1,
                new Particle.DustOptions(Color.RED, 1.3f),
                0.3
        );

        entity.addPotionEffect(potionEffect);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        this.regenerate(event.getBlock());
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        this.regenerate(event.blockList());
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        this.regenerate(event.blockList());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Block block = event.getEntity().getLocation().getBlock();

        if (this.region.isWithinRegion(block.getLocation())) {
            // Keep inventory.
            event.setKeepInventory(true);
            event.getDrops().clear();

            // Keep experience.
            event.setKeepLevel(true);
            event.setDroppedExp(0);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Location location = event.getPlayer().getLocation();

        if (this.region.isWithinRegion(location)) {
            event.setRespawnLocation(this.region.getRandomLocation());
        }
    }

    @EventHandler
    public void onEntityMove(EntityMoveEvent event) {
        Location location = event.getTo();
        LivingEntity entity = event.getEntity();
        CreatureSpawnEvent.SpawnReason spawnReason = event.getEntity().getEntitySpawnReason();
        boolean isEntityAllowedInRegion = this.getEntityIsAllowedInRegion(entity, spawnReason);

        if (!isEntityAllowedInRegion && this.region.isWithinRegion(location)) {
            this.setPotionEffectToBannedEntityInRegions(entity);
        }
    }

    @EventHandler
    public void onEntityTeleport(EntityTeleportEvent event) {
        Location location = event.getTo();
        Entity entity = event.getEntity();

        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }

        CreatureSpawnEvent.SpawnReason spawnReason = event.getEntity().getEntitySpawnReason();
        boolean isEntityAllowedInRegion = this.getEntityIsAllowedInRegion(livingEntity, spawnReason);

        if (location != null) {
            if (!isEntityAllowedInRegion && this.region.isWithinRegion(location)) {
                this.setPotionEffectToBannedEntityInRegions(livingEntity);
            }
        }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        Location location = event.getLocation();
        Entity entity = event.getEntity();

        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }

        CreatureSpawnEvent.SpawnReason spawnReason = event.getEntity().getEntitySpawnReason();
        boolean isEntityAllowedInRegion = this.getEntityIsAllowedInRegion(livingEntity, spawnReason);

        if (!isEntityAllowedInRegion && this.region.isWithinRegion(location)) {
            event.setCancelled(true);
        }
    }

}
