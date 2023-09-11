package io.github.riazufila.minedoplugin;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldguard.WorldGuard;
import io.github.riazufila.minedoplugin.constants.worldtype.WorldType;
import io.github.riazufila.minedoplugin.database.model.region.Region;
import io.github.riazufila.minedoplugin.itembuilder.ItemBuilder;
import io.github.riazufila.minedoplugin.customcommand.spawn.SpawnCommand;
import io.github.riazufila.minedoplugin.regionregeneration.RegionRegeneration;
import io.github.riazufila.minedoplugin.spawnlocationinitializer.SpawnLocationInitializer;
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
        new SpawnLocationInitializer(world);

        // Populate newly generated chests with Better Items.
        getServer().getPluginManager().registerEvents(new ItemBuilder(instance), instance);

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
