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

public class MinedoPlugin extends JavaPlugin {

    private static MinedoPlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        World world = getWorldInstance();

        // Set spawn location.
        new SpawnLocationInitializer(world);

        // Populate newly generated chests with Better Items.
        getServer().getPluginManager().registerEvents(new ItemBuilder(), this);

        // Spawn command and listeners.
        SpawnCommand spawnCommand = new SpawnCommand();
        getServer().getPluginManager().registerEvents(spawnCommand, this);
        Objects.requireNonNull(getCommand("spawn")).setExecutor(spawnCommand);

        // Spawn regenerations.
        WorldGuard worldGuard = getWorldGuardInstance();
        WorldEdit worldEdit = getWorldEditInstance();
        Region spawnRegion = new Region().getRegionByName("spawn");
        getServer().getPluginManager().registerEvents(
                new RegionRegeneration(world, worldGuard, worldEdit, spawnRegion), this
        );
    }

    public static MinedoPlugin getInstance() {
        return instance;
    }

    public static World getWorldInstance() {
        return Bukkit.getWorld(WorldType.WORLD.getType());
    }

    public static WorldGuard getWorldGuardInstance() {
        return WorldGuard.getInstance();
    }

    public static WorldEdit getWorldEditInstance() {
        return WorldEdit.getInstance();
    }

}
