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

public class Minedo extends JavaPlugin {

    @Override
    public void onEnable() {
        Minedo instance = this;

        World world = getWorldInstance();
        Server server = getPluginServer();

        // Set spawn location.
        SpawnLocationInitializer spawnLocationInitializer = new SpawnLocationInitializer(world);
        if (!spawnLocationInitializer.hasSpawnLocationSet()) {
            spawnLocationInitializer.setSpawnLocation();
        }

        // Populate newly generated chests with Better Items.
        server.getPluginManager().registerEvents(new ItemBuilder(instance), instance);

        // Register and display custom commands to players.
        CustomCommand customCommand = new CustomCommand(world, instance, server, this::getPluginCommand);
        customCommand.setupCustomCommands();

        // Region regenerations.
        WorldGuard worldGuard = getWorldGuardInstance();
        WorldEdit worldEdit = getWorldEditInstance();
        List<Region> regions = new Region().getAllRegions();

        for (Region region : regions) {
            RegionRegeneration regionRegeneration = new RegionRegeneration(
                    world, worldGuard, worldEdit, region, instance
            );

            // Initialize region setup.
            if (regionRegeneration.getRegion() == null) {
                regionRegeneration.setRegion();
                regionRegeneration.setRegionSnapshot();
            }

            // Initialize region schematics.
            if (!regionRegeneration.getRegionSnapshot()) {
                regionRegeneration.setRegionSnapshot();
            }

            server.getPluginManager().registerEvents(regionRegeneration, instance);
        }
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
