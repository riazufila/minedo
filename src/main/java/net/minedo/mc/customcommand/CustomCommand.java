package net.minedo.mc.customcommand;

import net.minedo.mc.Minedo;
import net.minedo.mc.customcommand.teleport.Teleport;
import net.minedo.mc.interfaces.customcommand.CustomCommandInterface;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.Objects;

public class CustomCommand implements Listener {

    private final Minedo pluginInstance;
    private final Server server;
    private final CustomCommandInterface customCommandInterface;

    public CustomCommand(Minedo pluginInstance, Server server, CustomCommandInterface customCommandInterface) {
        this.pluginInstance = pluginInstance;
        this.server = server;
        this.customCommandInterface = customCommandInterface;

        this.setupCustomCommands();
    }

    public void setupCustomCommands() {
        // Spawn command and listeners.
        Teleport teleport = new Teleport(this.pluginInstance);
        server.getPluginManager().registerEvents(teleport, this.pluginInstance);
        Objects.requireNonNull(this.customCommandInterface.getCommand("spawn")).setExecutor(teleport);
    }

    // Disable commands.
    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage();

        if (!player.isOp() && !command.substring(1).equals("spawn")) {
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
            event.getCommands().add("spawn");
        }
    }

}
