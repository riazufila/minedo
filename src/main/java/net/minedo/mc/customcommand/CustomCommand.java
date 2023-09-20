package net.minedo.mc.customcommand;

import net.minedo.mc.Minedo;
import net.minedo.mc.customcommand.teleport.player.PlayerTeleport;
import net.minedo.mc.customcommand.teleport.region.RegionTeleport;
import net.minedo.mc.interfaces.customcommand.CustomCommandInterface;
import net.minedo.mc.models.region.Region;
import net.minedo.mc.repositories.regionrepository.RegionRepository;
import org.bukkit.Server;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CustomCommand {

    private final World world;
    private final Minedo pluginInstance;
    private final Server server;
    private final CustomCommandInterface customCommandInterface;
    private final List<UUID> globalTeleportingPlayers = new ArrayList<>();

    public CustomCommand(
            World world, Minedo pluginInstance, Server server, CustomCommandInterface customCommandInterface
    ) {
        this.world = world;
        this.pluginInstance = pluginInstance;
        this.server = server;
        this.customCommandInterface = customCommandInterface;
    }

    public void setupCustomCommands() {
        RegionRepository regionRepository = new RegionRepository();
        List<Region> regions = regionRepository.getAllRegions();

        // Setup region teleport commands.
        for (Region region : regions) {
            String customCommand = region.getName().toLowerCase();

            // Setup command and listener.
            RegionTeleport regionTeleport = new RegionTeleport(
                    region, customCommand, globalTeleportingPlayers, this.world, this.pluginInstance
            );
            server.getPluginManager().registerEvents(regionTeleport, this.pluginInstance);
            Objects.requireNonNull(this.customCommandInterface.getCommand(customCommand)).setExecutor(regionTeleport);
        }

        // Player teleport.
        PlayerTeleport playerTeleport = new PlayerTeleport(globalTeleportingPlayers, this.pluginInstance);
        server.getPluginManager().registerEvents(playerTeleport, this.pluginInstance);
        this.customCommandInterface.getCommand("teleport").setExecutor(playerTeleport);
    }

}
