package net.minedo.mc.customcommand.teleport.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minedo.mc.Minedo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerTeleport implements CommandExecutor {

    private final Minedo pluginInstance;
    private final Map<UUID, Integer> teleportingPlayers = new HashMap<>();

    public PlayerTeleport(Minedo pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args
    ) {
        if (!(sender instanceof Player requester)) {
            return true;
        }

        // Validate command.
        if (
                args.length == 0
                        || !(
                        args[0].equals("request")
                                || args[0].equals("accept")
                                || args[0].equals("decline")
                )
                        || (args.length == 1 && args[0].equals("request"))
                        || (args.length > 2 && args[0].equals("request"))
                        || (args.length > 1 && !args[0].equals("request"))
        ) {
            requester.sendMessage(Component
                    .text("Usage: /teleport <request <player> | accept | decline>")
                    .color(NamedTextColor.GRAY)
            );

            return true;
        }

        switch (args[0]) {
            case "request":
                String teleportTarget = args[1];
                Player requestee = this.pluginInstance.getServer().getPlayer(teleportTarget);

                if (requestee != null) {
                    requestee.sendMessage(Component
                            .text(String.format("%s is requesting to teleport..", requester.getName()))
                            .color(NamedTextColor.YELLOW)
                    );
                } else {
                    requester.sendMessage(Component
                            .text(String.format("%s is not in the server.", teleportTarget))
                            .color(NamedTextColor.RED)
                    );
                }

                return true;

            case "accept", "decline":
                return true;

            default:
                requester.sendMessage(Component
                        .text("Usage: /teleport <request <player> | accept | decline>")
                        .color(NamedTextColor.GRAY)
                );
                return true;
        }
    }

}
