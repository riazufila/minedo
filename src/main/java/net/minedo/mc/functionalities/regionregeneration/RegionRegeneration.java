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
import net.minedo.mc.constants.directory.Directory;
import net.minedo.mc.constants.filetype.FileType;
import net.minedo.mc.interfaces.chunkprocessor.ChunkProcessor;
import net.minedo.mc.models.region.Region;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Flying;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Logger;

public class RegionRegeneration implements Listener {

    private final Region region;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final HashMap<String, Integer> restoringChunks = new HashMap<>();

    public RegionRegeneration(Region region) {
        this.region = region;
    }

    private File getFile(int chunkX, int chunkZ) {
        String chunkCoordinate = String.format("%d,%d", chunkX, chunkZ);

        String fileName = String.format(
                "%s-region-(%s).%s",
                this.region.getName().toLowerCase(),
                chunkCoordinate,
                FileType.SCHEMATIC.getType()
        );

        return new File(Directory.SCHEMATIC.getDirectory() + fileName);
    }

    private boolean processChunks(World world, ChunkProcessor chunkProcessor, boolean isGetter) {
        int CHUNK_SIZE = Common.CHUNK_SIZE.getValue();
        int minX = this.region.getMinX();
        int maxX = this.region.getMaxX();
        int minZ = this.region.getMinZ();
        int maxZ = this.region.getMaxZ();

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

    private boolean checkSnapshotFileExists(Object[] params) {
        int chunkX = (int) params[0];
        int chunkZ = (int) params[1];

        File file = this.getFile(chunkX, chunkZ);

        if (!file.exists()) {
            this.logger.severe(String.format(
                    "Unable to get %s region snapshot.", this.region.getName()
            ));

            return false;
        }

        return true;
    }

    private boolean createSnapshotFile(Object[] params) {
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
            throw new RuntimeException(e);
        }

        File file = this.getFile(chunkX, chunkZ);

        try (ClipboardWriter clipboardWriter = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(
                new FileOutputStream(file)
        )) {
            clipboardWriter.write(blockArrayClipboard);
        } catch (IOException e) {
            this.logger.severe(
                    String.format(
                            "Unable to set %s region snapshot: %s",
                            this.region.getName(),
                            e.getMessage()
                    )
            );
            throw new RuntimeException(e);
        }

        return true;
    }

    public boolean getRegionSnapshot() {
        this.logger.info(String.format("Getting %s region snapshot.", this.region.getName()));

        return processChunks(this.region.getWorldType(), this::checkSnapshotFileExists, true);
    }

    public void setRegionSnapshot() {
        this.logger.info(String.format("Setting %s region snapshot.", this.region.getName()));
        processChunks(this.region.getWorldType(), this::createSnapshotFile, false);
    }

    public void restoreRegionChunk(Chunk chunk) {
        String restoringChunkKey = String.format("(%d,%d)", chunk.getX(), chunk.getZ());

        if (restoringChunks.containsKey(restoringChunkKey)) {
            Bukkit.getScheduler().cancelTask(restoringChunks.get(restoringChunkKey));
        }

        // Run region regeneration scheduler after 30 seconds.
        RegionRegenerationLauncher regionRegenerationLauncher = new RegionRegenerationLauncher(
                chunk, this.region, this.restoringChunks
        );

        int restoringTaskId = regionRegenerationLauncher
                .runTaskLater(Minedo.getInstance(), 600)
                .getTaskId();

        // Place task ID and update restoring chunk details.
        restoringChunks.put(restoringChunkKey, restoringTaskId);
    }

    private boolean isWithinRegion(Location location) {
        World world = this.region.getWorldType();

        if (world != location.getWorld()) {
            return false;
        }

        int locationBlockX = location.getBlockX();
        int locationBlockZ = location.getBlockZ();

        return locationBlockX >= this.region.getMinX()
                && locationBlockX <= this.region.getMaxX()
                && locationBlockZ >= this.region.getMinZ()
                && locationBlockZ <= this.region.getMaxZ();
    }

    private void regenerate(Block block) {
        Chunk chunk = block.getChunk();

        // Check if block destroyed is in region.
        if (!isWithinRegion(block.getLocation())) {
            return;
        }

        // Restore chunk.
        restoreRegionChunk(chunk);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        this.regenerate(event.getBlock());
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        this.regenerate(event.getLocation().getBlock());
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        this.regenerate(event.getBlock());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Block block = event.getEntity().getLocation().getBlock();

        if (isWithinRegion(block.getLocation())) {
            // Keep inventory.
            event.setKeepInventory(true);
            event.getDrops().clear();

            // Keep experience.
            event.setKeepLevel(true);
            event.setDroppedExp(0);
        }
    }

    public void spawnParticleOnEntity(Entity entity, Particle particle, double density, int count) {
        BoundingBox boundingBox = entity.getBoundingBox();
        World world = entity.getWorld();

        // Calculate the step size based on the density
        double stepX = boundingBox.getWidthX() / density;
        double stepY = boundingBox.getHeight() / density;
        double stepZ = boundingBox.getWidthZ() / density;

        // Iterate over the bounding box and spawn particles
        for (double x = boundingBox.getMinX(); x <= boundingBox.getMaxX(); x += stepX) {
            for (double y = boundingBox.getMinY(); y <= boundingBox.getMaxY(); y += stepY) {
                for (double z = boundingBox.getMinZ(); z <= boundingBox.getMaxZ(); z += stepZ) {
                    Location particleLocation = new Location(world, x, y, z);
                    world.spawnParticle(particle, particleLocation, count);
                }
            }
        }
    }

    @EventHandler
    public void onEntityMove(EntityMoveEvent event) {
        Location location = event.getTo();
        LivingEntity entity = event.getEntity();

        if (isWithinRegion(location)
                && (entity instanceof Monster || entity instanceof Flying)) {
            Location regionCenter = this.region.getCenter();
            Vector awayFromCenter = location.toVector().subtract(regionCenter.toVector()).normalize();
            double MULTIPLIER = 1.0;

            entity.getWorld().playSound(location, Sound.BLOCK_AMETHYST_BLOCK_HIT, 1, 1);
            this.spawnParticleOnEntity(entity, Particle.CRIT_MAGIC, 1, 5);
            entity.setVelocity(awayFromCenter.multiply(MULTIPLIER));
        }
    }

    @EventHandler
    public void onEntityTeleport(EntityTeleportEvent event) {
        Location location = event.getTo();
        Entity entity = event.getEntity();

        if (isWithinRegion(Objects.requireNonNull(location))
                && (entity instanceof Monster || entity instanceof Flying)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Location location = event.getPlayer().getLocation();

        if (isWithinRegion(location)) {
            event.setRespawnLocation(this.region.getRandomLocation());
        }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        Location location = event.getLocation();
        Entity entity = event.getEntity();

        if (isWithinRegion(location)
                && (entity instanceof Monster || entity instanceof Flying)) {
            event.setCancelled(true);
        }
    }

}
