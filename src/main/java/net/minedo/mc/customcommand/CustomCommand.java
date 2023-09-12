package net.minedo.mc.customcommand;

import net.minedo.mc.Minedo;
import net.minedo.mc.customcommand.teleport.Teleport;
import net.minedo.mc.database.model.region.Region;
import net.minedo.mc.interfaces.customcommand.CustomCommandInterface;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomCommand implements Listener {

    private final World world;
    private final Minedo pluginInstance;
    private final Server server;
    private final CustomCommandInterface customCommandInterface;
    private List<String> customCommands;
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
        List<String> customCommands = new ArrayList<>();

        // Setup region teleport commands.
        for (Region region : regions) {
            String customCommand = region.getName().toLowerCase();

            // Add the regions as commands for teleport.
            customCommands.add(customCommand);

            // Setup command and listener.
            Teleport teleport = new Teleport(
                    region.getMinX(), region.getMaxX(), region.getMinZ(), region.getMaxZ(),
                    customCommand, this.world, this.pluginInstance
            );
            server.getPluginManager().registerEvents(teleport, this.pluginInstance);
            Objects.requireNonNull(this.customCommandInterface.getCommand(customCommand)).setExecutor(teleport);
        }

        this.customCommands = customCommands;
    }

    // Disable commands.
    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage();

        if (!player.isOp() && !this.customCommands.contains(command.substring(1))) {
            event.setCancelled(true);
        }
    }

    // Remove commands from display.
    @EventHandler
    public void onPlayerCommandSend(PlayerCommandSendEvent event) {
        Player player = event.getPlayer();

        if (!player.isOp()) {
            // Remove all commands.
            event.getCommands().clear();

            // Add custom commands to be displayed.
            event.getCommands().addAll(this.customCommands);
        }
    }

}
