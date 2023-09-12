package net.minedo.mc;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldguard.WorldGuard;
import net.minedo.mc.constants.worldtype.WorldType;
import net.minedo.mc.database.model.region.Region;
import net.minedo.mc.itembuilder.ItemBuilder;
import net.minedo.mc.customcommand.spawn.SpawnCommand;
import net.minedo.mc.regionregeneration.RegionRegeneration;
import net.minedo.mc.spawnlocationinitializer.SpawnLocationInitializer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Logger;

public class Minedo extends JavaPlugin {

    private static Minedo instance;

    @Override
    public void onEnable() {
        instance = this;

        World world = getWorldInstance();
        Logger logger = getPluginLogger();

        // Set spawn location.
        new SpawnLocationInitializer(world, logger);

        // Populate newly generated chests with Better Items.
        getServer().getPluginManager().registerEvents(new ItemBuilder(logger, instance), instance);

        // Spawn command and listeners.
        SpawnCommand spawnCommand = new SpawnCommand(instance);
        getServer().getPluginManager().registerEvents(spawnCommand, instance);
        Objects.requireNonNull(getCommand("spawn")).setExecutor(spawnCommand);

        // Spawn regenerations.
        WorldGuard worldGuard = getWorldGuardInstance();
        WorldEdit worldEdit = getWorldEditInstance();
        Region spawnRegion = new Region().getRegionByName("spawn");
        getServer().getPluginManager().registerEvents(
                new RegionRegeneration(world, worldGuard, worldEdit, spawnRegion, instance, logger),
                instance
        );
    }

    public Logger getPluginLogger() {
        return getLogger();
    }

    public World getWorldInstance() {
        return Bukkit.getWorld(WorldType.WORLD.getType());
    }

    public WorldGuard getWorldGuardInstance() {
        return WorldGuard.getInstance();
    }

    public WorldEdit getWorldEditInstance() {
        return WorldEdit.getInstance();
    }

}
