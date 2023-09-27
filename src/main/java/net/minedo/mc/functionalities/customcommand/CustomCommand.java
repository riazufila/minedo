package net.minedo.mc.functionalities.customcommand;

import net.minedo.mc.Minedo;
import net.minedo.mc.constants.customcommandtype.CustomCommandType;
import net.minedo.mc.functionalities.customcommand.ignore.Ignore;
import net.minedo.mc.functionalities.customcommand.like.Like;
import net.minedo.mc.functionalities.customcommand.message.Message;
import net.minedo.mc.functionalities.customcommand.narrate.Narrate;
import net.minedo.mc.functionalities.customcommand.teleport.player.PlayerTeleport;
import net.minedo.mc.functionalities.customcommand.teleport.region.RegionTeleport;
import net.minedo.mc.models.region.Region;
import net.minedo.mc.repositories.regionrepository.RegionRepository;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CustomCommand {

    private final Minedo pluginInstance;
    private final List<UUID> globalTeleportingPlayers = new ArrayList<>();

    public CustomCommand(Minedo pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    public void setupCustomCommands() {
        Server server = this.pluginInstance.getServer();
        RegionRepository regionRepository = new RegionRepository(this.pluginInstance);
        List<Region> regions = regionRepository.getAllRegions();

        // Region teleport.
        for (Region region : regions) {
            RegionTeleport regionTeleport = new RegionTeleport(
                    region, regions, globalTeleportingPlayers, this.pluginInstance
            );

            server.getPluginManager().registerEvents(regionTeleport, this.pluginInstance);

            Objects.requireNonNull(this.pluginInstance.getCommand(
                    CustomCommandType.REGION_TELEPORT.getMessage()
            )).setExecutor(regionTeleport);
        }

        // Player teleport.
        PlayerTeleport playerTeleport = new PlayerTeleport(globalTeleportingPlayers, this.pluginInstance);
        server.getPluginManager().registerEvents(playerTeleport, this.pluginInstance);

        Objects.requireNonNull(this.pluginInstance.getCommand(
                CustomCommandType.PLAYER_TELEPORT.getMessage()
        )).setExecutor(playerTeleport);

        // Narrate.
        Objects.requireNonNull(this.pluginInstance.getCommand(
                CustomCommandType.NARRATE.getMessage()
        )).setExecutor(new Narrate(this.pluginInstance));

        // Like.
        Objects.requireNonNull(this.pluginInstance.getCommand(
                CustomCommandType.LIKE.getMessage()
        )).setExecutor(new Like(this.pluginInstance));

        // Ignore.
        Objects.requireNonNull(this.pluginInstance.getCommand(
                CustomCommandType.IGNORE.getMessage()
        )).setExecutor(new Ignore(this.pluginInstance));

        // Message.
        Objects.requireNonNull(this.pluginInstance.getCommand(
                CustomCommandType.MESSAGE.getMessage()
        )).setExecutor(new Message(this.pluginInstance));
    }

}
