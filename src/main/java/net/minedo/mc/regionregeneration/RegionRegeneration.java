package net.minedo.mc.regionregeneration;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import io.papermc.paper.event.entity.EntityMoveEvent;
import net.minedo.mc.Minedo;
import net.minedo.mc.constants.common.Common;
import net.minedo.mc.constants.directory.Directory;
import net.minedo.mc.constants.filetype.FileType;
import net.minedo.mc.models.region.Region;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class RegionRegeneration implements Listener {

    private final Region region;
    private final Minedo pluginInstance;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final Map<String, Integer> restoringChunks = new HashMap<>();

    public RegionRegeneration(Region region, Minedo pluginInstance) {
        this.region = region;
        this.pluginInstance = pluginInstance;
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

    public ProtectedRegion getRegion() {
        this.logger.info(String.format("Getting %s region.", this.region.getName()));

        ProtectedRegion protectedRegion = null;
        WorldGuard worldGuard = pluginInstance.getWorldGuard();
        List<World> worlds = pluginInstance.getAllWorlds();
        RegionContainer regionContainer = worldGuard.getPlatform().getRegionContainer();

        for (World world : worlds) {
            RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(world));
            protectedRegion = Objects.requireNonNull(regionManager).getRegion(region.getName());

            if (protectedRegion != null) {
                break;
            }
        }

        if (protectedRegion == null) {
            this.logger.severe(String.format("Unable to get %s region.", this.region.getName()));

            return null;
        }

        return protectedRegion;
    }

    public void setRegion() {
        this.logger.info(String.format("Setting %s region.", this.region.getName()));

        WorldGuard worldGuard = pluginInstance.getWorldGuard();
        ProtectedRegion protectedRegion = getProtectedRegion();
        World world = this.region.getWorldType();

        // Set permissions.
        protectedRegion.setFlag(Flags.BUILD, StateFlag.State.ALLOW);
        protectedRegion.setFlag(Flags.BLOCK_BREAK, StateFlag.State.ALLOW);
        protectedRegion.setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY);

        RegionContainer regionContainer = worldGuard.getPlatform().getRegionContainer();

        Objects.requireNonNull(regionContainer.get(BukkitAdapter.adapt(world))).addRegion(protectedRegion);
    }

    @NotNull
    private ProtectedRegion getProtectedRegion() {
        World world = this.region.getWorldType();

        BlockVector3 min = BlockVector3.at(
                region.getMinX(),
                world.getMinHeight(),
                region.getMinZ()
        );

        BlockVector3 max = BlockVector3.at(
                region.getMaxX(),
                world.getMaxHeight(),
                region.getMaxZ()
        );

        return new ProtectedCuboidRegion(region.getName(), min, max);
    }

    public boolean getRegionSnapshot() {
        this.logger.info(String.format("Getting %s region snapshot.", this.region.getName()));

        WorldGuard worldGuard = pluginInstance.getWorldGuard();
        List<World> worlds = pluginInstance.getAllWorlds();
        RegionContainer container = worldGuard.getPlatform().getRegionContainer();

        for (World world : worlds) {
            RegionManager regionManager = container.get(BukkitAdapter.adapt(world));
            ProtectedRegion protectedRegion = Objects.requireNonNull(regionManager).getRegion(this.region.getName());

            if (protectedRegion != null) {
                int chunkSize = Common.CHUNK_SIZE.getValue();
                int minX = protectedRegion.getMinimumPoint().getBlockX();
                int maxX = protectedRegion.getMaximumPoint().getBlockX();
                int minZ = protectedRegion.getMinimumPoint().getBlockZ();
                int maxZ = protectedRegion.getMaximumPoint().getBlockZ();

                // Iterate through chunks and check if a schematic for each chunk has been created.
                for (int chunkX = minX / chunkSize; chunkX <= maxX / chunkSize; chunkX++) {
                    for (int chunkZ = minZ / chunkSize; chunkZ <= maxZ / chunkSize; chunkZ++) {
                        int chunkMinX = chunkX * chunkSize;
                        int chunkMaxX = chunkMinX + chunkSize - 1;
                        int chunkMinZ = chunkZ * chunkSize;
                        int chunkMaxZ = chunkMinZ + chunkSize - 1;

                        if (chunkMinX < chunkMaxX && chunkMinZ < chunkMaxZ) {
                            File file = this.getFile(chunkX, chunkZ);

                            if (!file.exists()) {
                                this.logger.severe(String.format(
                                        "Unable to get %s region snapshot.", this.region.getName()
                                ));

                                return false;
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    public void setRegionSnapshot() {
        this.logger.info(String.format("Setting %s region snapshot.", this.region.getName()));

        WorldGuard worldGuard = this.pluginInstance.getWorldGuard();
        RegionContainer container = worldGuard.getPlatform().getRegionContainer();
        List<World> worlds = this.pluginInstance.getAllWorlds();

        for (World world : worlds) {
            RegionManager regionManager = container.get(BukkitAdapter.adapt(world));
            ProtectedRegion protectedRegion = Objects.requireNonNull(regionManager).getRegion(this.region.getName());

            if (protectedRegion != null) {
                int chunkSize = Common.CHUNK_SIZE.getValue();
                int minX = protectedRegion.getMinimumPoint().getBlockX();
                int maxX = protectedRegion.getMaximumPoint().getBlockX();
                int minZ = protectedRegion.getMinimumPoint().getBlockZ();
                int maxZ = protectedRegion.getMaximumPoint().getBlockZ();

                // Iterate through chunks and create a schematic for each chunk.
                for (int chunkX = minX / chunkSize; chunkX <= maxX / chunkSize; chunkX++) {
                    for (int chunkZ = minZ / chunkSize; chunkZ <= maxZ / chunkSize; chunkZ++) {
                        int chunkMinX = chunkX * chunkSize;
                        int chunkMaxX = chunkMinX + chunkSize - 1;
                        int chunkMinZ = chunkZ * chunkSize;
                        int chunkMaxZ = chunkMinZ + chunkSize - 1;

                        if (chunkMinX < chunkMaxX && chunkMinZ < chunkMaxZ) {
                            CuboidRegion cuboidRegion = new CuboidRegion(
                                    BukkitAdapter.adapt(world),
                                    BlockVector3.at(chunkMinX, world.getMinHeight(), chunkMinZ),
                                    BlockVector3.at(chunkMaxX, world.getMaxHeight(), chunkMaxZ)
                            );

                            BlockArrayClipboard blockArrayClipboard = new BlockArrayClipboard(cuboidRegion);
                            WorldEdit worldEdit = this.pluginInstance.getWorldEdit();
                            EditSession editSession = worldEdit.newEditSession(BukkitAdapter.adapt(world));

                            ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                                    editSession,
                                    cuboidRegion,
                                    blockArrayClipboard,
                                    cuboidRegion.getMinimumPoint()
                            );

                            Operations.complete(forwardExtentCopy);

                            File file = this.getFile(chunkX, chunkZ);

                            try (ClipboardWriter clipboardWriter = BuiltInClipboardFormat.FAST.getWriter(
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
                        }
                    }
                }
            }
        }
    }

    public void restoreRegionChunk(Chunk chunk) {
        String restoringChunkKey = String.format("(%d,%d)", chunk.getX(), chunk.getZ());

        if (restoringChunks.containsKey(restoringChunkKey)) {
            Bukkit.getScheduler().cancelTask(restoringChunks.get(restoringChunkKey));
        }

        // Run region regeneration scheduler after 30 seconds.
        RegionRegenerationLauncher regionRegenerationLauncher = new RegionRegenerationLauncher(
                chunk, this.region, this.restoringChunks, this.pluginInstance
        );

        int restoringTaskId = regionRegenerationLauncher
                .runTaskLater(this.pluginInstance, 600)
                .getTaskId();

        // Place task ID and update restoring chunk details.
        restoringChunks.put(restoringChunkKey, restoringTaskId);
    }

    private boolean isWithinRegion(Location location) {
        WorldGuard worldGuard = this.pluginInstance.getWorldGuard();
        ApplicableRegionSet applicableRegionSet = worldGuard
                .getPlatform()
                .getRegionContainer()
                .createQuery()
                .getApplicableRegions(BukkitAdapter.adapt(location));

        return !applicableRegionSet.getRegions().isEmpty();
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
    public void onEntityMoveEvent(EntityMoveEvent event) {
        Location location = event.getTo();
        LivingEntity entity = event.getEntity();

        if (isWithinRegion(location) && entity instanceof Monster) {
            Location regionCenter = this.region.getCenter();
            Vector awayFromCenter = location.toVector().subtract(regionCenter.toVector()).normalize();
            double MULTIPLIER = 1.0;

            entity.getWorld().playSound(location, Sound.BLOCK_AMETHYST_BLOCK_HIT, 1, 1);
            this.spawnParticleOnEntity(entity, Particle.CRIT_MAGIC, 1, 15);
            entity.setVelocity(awayFromCenter.multiply(MULTIPLIER));
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Location location = event.getPlayer().getLocation();

        if (isWithinRegion(location)) {
            event.setRespawnLocation(this.region.getRandomLocation());
        }
    }

}
