package net.minedo.mc.functionalities.customcommand;

import net.minedo.mc.Minedo;
import net.minedo.mc.constants.command.type.customcommandtype.CustomCommandType;
import net.minedo.mc.functionalities.customcommand.color.Color;
import net.minedo.mc.functionalities.customcommand.ignore.Ignore;
import net.minedo.mc.functionalities.customcommand.like.Like;
import net.minedo.mc.functionalities.customcommand.message.Message;
import net.minedo.mc.functionalities.customcommand.narrate.Narrate;
import net.minedo.mc.functionalities.customcommand.nickname.Nickname;
import net.minedo.mc.functionalities.customcommand.teleport.home.HomeTeleport;
import net.minedo.mc.functionalities.customcommand.teleport.player.PlayerTeleport;
import net.minedo.mc.functionalities.customcommand.teleport.region.RegionTeleport;
import net.minedo.mc.models.region.Region;
import net.minedo.mc.repositories.regionrepository.RegionRepository;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CustomCommand {

    private final List<UUID> globalTeleportingPlayers = new ArrayList<>();

    public void setupCustomCommands() {
        Minedo instance = Minedo.getInstance();
        PluginManager pluginManager = instance.getServer().getPluginManager();
        List<Region> regions = RegionRepository.getAllRegions();

        // Region teleport.
        for (Region region : regions) {
            RegionTeleport regionTeleport = new RegionTeleport(region, regions, globalTeleportingPlayers);
            PluginCommand regionTeleportCommand = instance.getCommand(CustomCommandType.REGION_TELEPORT.getMessage());
            Objects.requireNonNull(regionTeleportCommand).setExecutor(regionTeleport);
            pluginManager.registerEvents(regionTeleport, instance);
        }

        // Player teleport.
        PlayerTeleport playerTeleport = new PlayerTeleport(globalTeleportingPlayers);
        PluginCommand playerTeleportCommand = instance.getCommand(CustomCommandType.PLAYER_TELEPORT.getMessage());
        Objects.requireNonNull(playerTeleportCommand).setExecutor(playerTeleport);
        pluginManager.registerEvents(playerTeleport, instance);

        // Home teleport.
        HomeTeleport homeTeleport = new HomeTeleport(globalTeleportingPlayers);
        PluginCommand homeTeleportCommand = instance.getCommand(CustomCommandType.HOME_TELEPORT.getMessage());
        Objects.requireNonNull(homeTeleportCommand).setExecutor(homeTeleport);
        pluginManager.registerEvents(homeTeleport, instance);

        // Narrate.
        Objects.requireNonNull(instance.getCommand(CustomCommandType.NARRATE.getMessage())).setExecutor(new Narrate());

        // Like.
        Objects.requireNonNull(instance.getCommand(CustomCommandType.LIKE.getMessage())).setExecutor(new Like());

        // Ignore.
        Objects.requireNonNull(instance.getCommand(CustomCommandType.IGNORE.getMessage())).setExecutor(new Ignore());

        // Message.
        Objects.requireNonNull(instance.getCommand(CustomCommandType.MESSAGE.getMessage())).setExecutor(new Message());

        // Color.
        Objects.requireNonNull(instance.getCommand(CustomCommandType.COLOR.getMessage())).setExecutor(new Color());

        // Nickname.
        Nickname nickname = new Nickname();
        Objects.requireNonNull(instance.getCommand(CustomCommandType.NICKNAME.getMessage())).setExecutor(nickname);
        pluginManager.registerEvents(nickname, instance);
    }

}
