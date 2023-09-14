package net.minedo.mc;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldguard.WorldGuard;
import net.minedo.mc.constants.worldtype.WorldType;
import net.minedo.mc.customcommand.CustomCommand;
import net.minedo.mc.database.model.region.Region;
import net.minedo.mc.itembuilder.ItemBuilder;
import net.minedo.mc.regionregeneration.RegionRegeneration;
import net.minedo.mc.spawnlocationinitializer.SpawnLocationInitializer;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Logger;

public class Minedo extends JavaPlugin {

    @Override
    public void onEnable() {
        Minedo instance = this;

        World world = getWorldInstance();
        Logger logger = getPluginLogger();
        Server server = getPluginServer();

        // Set spawn location.
        new SpawnLocationInitializer(world, logger);

        // Populate newly generated chests with Better Items.
        server.getPluginManager().registerEvents(new ItemBuilder(logger, instance), instance);

        // Register and display custom commands to players.
        new CustomCommand(world, instance, server, this::getPluginCommand);

        // Region regenerations.
        WorldGuard worldGuard = getWorldGuardInstance();
        WorldEdit worldEdit = getWorldEditInstance();
        List<Region> regions = new Region().getAllRegions();

        for (Region region : regions) {
            RegionRegeneration regionRegeneration = new RegionRegeneration(
                    world, worldGuard, worldEdit, region, instance, logger
            );

            server.getPluginManager().registerEvents(regionRegeneration, instance);
        }
    }

    public Logger getPluginLogger() {
        return getLogger();
    }

    public Server getPluginServer() {
        return getServer();
    }

    public PluginCommand getPluginCommand(String command) {
        return getCommand(command);
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
