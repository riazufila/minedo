package net.minedo.mc.customcommand;

import net.minedo.mc.Minedo;
import net.minedo.mc.customcommand.teleport.player.PlayerTeleport;
import net.minedo.mc.customcommand.teleport.region.RegionTeleport;
import net.minedo.mc.database.model.region.Region;
import net.minedo.mc.interfaces.customcommand.CustomCommandInterface;
import org.bukkit.Server;
import org.bukkit.World;

import java.util.List;
import java.util.Objects;

public class CustomCommand {

    private final World world;
    private final Minedo pluginInstance;
    private final Server server;
    private final CustomCommandInterface customCommandInterface;
    private final List<Region> regions;

    public CustomCommand(
            World world, Minedo pluginInstance, Server server, CustomCommandInterface customCommandInterface
    ) {
        this.world = world;
        this.pluginInstance = pluginInstance;
        this.server = server;
        this.customCommandInterface = customCommandInterface;
        this.regions = new Region().getAllRegions();

        this.setupCustomCommands();
    }

    public void setupCustomCommands() {
        // Setup region teleport commands.
        for (Region region : regions) {
            String customCommand = region.getName().toLowerCase();

            // Setup command and listener.
            RegionTeleport regionTeleport = new RegionTeleport(
                    region.getMinX(), region.getMaxX(), region.getMinZ(), region.getMaxZ(),
                    customCommand, this.world, this.pluginInstance
            );
            server.getPluginManager().registerEvents(regionTeleport, this.pluginInstance);
            Objects.requireNonNull(this.customCommandInterface.getCommand(customCommand)).setExecutor(regionTeleport);
        }

        // Player teleport.
        PlayerTeleport playerTeleport = new PlayerTeleport(this.pluginInstance);
        server.getPluginManager().registerEvents(playerTeleport, this.pluginInstance);
        this.customCommandInterface.getCommand("teleport").setExecutor(playerTeleport);
    }

}
