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
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Custom command handler.
 */
public class CustomCommand {

    private final List<UUID> globalTeleportingPlayers = new ArrayList<>();

    /**
     * Set command executor.
     *
     * @param pluginCommand plugin command
     * @param command       command
     */
    private void setCommandExecutor(PluginCommand pluginCommand, Object command) {
        if (command instanceof CommandExecutor commandExecutor) {
            pluginCommand.setExecutor(commandExecutor);
        }
    }

    /**
     * Setup custom commands.
     */
    public void setupCustomCommands() {
        Minedo instance = Minedo.getInstance();
        PluginManager pluginManager = instance.getServer().getPluginManager();
        List<Region> regions = RegionRepository.getAllRegions();

        // Region teleport.
        if (regions != null) {
            for (Region region : regions) {
                RegionTeleport regionTeleport = new RegionTeleport(region, regions, globalTeleportingPlayers);
                this.setCommandExecutor(
                        instance.getCommand(CustomCommandType.REGION_TELEPORT.getMessage()),
                        regionTeleport
                );
                pluginManager.registerEvents(regionTeleport, instance);
            }
        }

        // Player teleport.
        PlayerTeleport playerTeleport = new PlayerTeleport(globalTeleportingPlayers);
        this.setCommandExecutor(instance.getCommand(CustomCommandType.PLAYER_TELEPORT.getMessage()), playerTeleport);
        pluginManager.registerEvents(playerTeleport, instance);

        // Home teleport.
        HomeTeleport homeTeleport = new HomeTeleport(globalTeleportingPlayers);
        this.setCommandExecutor(instance.getCommand(CustomCommandType.HOME_TELEPORT.getMessage()), homeTeleport);
        pluginManager.registerEvents(homeTeleport, instance);

        // Narrate.
        this.setCommandExecutor(instance.getCommand(CustomCommandType.NARRATE.getMessage()), new Narrate());

        // Like.
        this.setCommandExecutor(instance.getCommand(CustomCommandType.LIKE.getMessage()), new Like());

        // Ignore.
        this.setCommandExecutor(instance.getCommand(CustomCommandType.IGNORE.getMessage()), new Ignore());

        // Message.
        this.setCommandExecutor(instance.getCommand(CustomCommandType.MESSAGE.getMessage()), new Message());

        // Color.
        this.setCommandExecutor(instance.getCommand(CustomCommandType.COLOR.getMessage()), new Color());

        // Nickname.
        Nickname nickname = new Nickname();
        this.setCommandExecutor(instance.getCommand(CustomCommandType.NICKNAME.getMessage()), nickname);
        pluginManager.registerEvents(nickname, instance);
    }

}
