package net.minedo.mc.customcommand;

import net.minedo.mc.Minedo;
import net.minedo.mc.constants.customcommandtype.CustomCommandType;
import net.minedo.mc.customcommand.teleport.player.PlayerTeleport;
import net.minedo.mc.customcommand.teleport.region.RegionTeleport;
import net.minedo.mc.interfaces.customcommand.CustomCommandInterface;
import net.minedo.mc.models.region.Region;
import net.minedo.mc.repositories.regionrepository.RegionRepository;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CustomCommand {

    private final Minedo pluginInstance;
    private final CustomCommandInterface customCommandInterface;
    private final List<UUID> globalTeleportingPlayers = new ArrayList<>();

    public CustomCommand(Minedo pluginInstance, CustomCommandInterface customCommandInterface) {
        this.pluginInstance = pluginInstance;
        this.customCommandInterface = customCommandInterface;
    }

    public void setupCustomCommands() {
        Server server = this.pluginInstance.getPluginServer();
        RegionRepository regionRepository = new RegionRepository(this.pluginInstance);
        List<Region> regions = regionRepository.getAllRegions();

        // Region teleport.
        for (Region region : regions) {
            RegionTeleport regionTeleport = new RegionTeleport(region, globalTeleportingPlayers, this.pluginInstance);
            server.getPluginManager().registerEvents(regionTeleport, this.pluginInstance);

            Objects.requireNonNull(this.customCommandInterface.getCommand(
                    region.getName().toLowerCase()
            )).setExecutor(regionTeleport);
        }

        // Player teleport.
        PlayerTeleport playerTeleport = new PlayerTeleport(globalTeleportingPlayers, this.pluginInstance);
        server.getPluginManager().registerEvents(playerTeleport, this.pluginInstance);

        this.customCommandInterface.getCommand(
                CustomCommandType.PLAYER_TELEPORT.getMessage()
        ).setExecutor(playerTeleport);
    }

}
