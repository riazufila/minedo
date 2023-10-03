package net.minedo.mc.functionalities.customcommand.teleport.home;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minedo.mc.constants.command.message.globalteleportmessage.GlobalTeleportMessage;
import net.minedo.mc.constants.command.message.hometeleportmessage.HomeTeleportMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class HomeTeleport implements CommandExecutor, Listener, TabCompleter {

    private final List<UUID> globalTeleportingPlayers;
    private final HashMap<UUID, Integer> teleportingPlayers = new HashMap<>();

    public HomeTeleport(List<UUID> globalTeleportingPlayers) {
        this.globalTeleportingPlayers = globalTeleportingPlayers;
    }

    private boolean isCommandValid(String[] args) {
        // TODO: Validate command.

        return true;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args
    ) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (!this.isCommandValid(args)) {
            player.sendMessage(Component
                    .text(String.format(HomeTeleportMessage.ERROR_USAGE.getMessage()))
                    .color(NamedTextColor.GRAY)
            );

            return true;
        }

        if (
                globalTeleportingPlayers.contains(player.getUniqueId())
                        || teleportingPlayers.containsKey(player.getUniqueId())
        ) {
            player.sendMessage(Component
                    .text(GlobalTeleportMessage.ERROR_USE_MORE_THAN_ONCE_AT_A_TIME.getMessage())
                    .color(NamedTextColor.RED)
            );

            return true;
        }

        // TODO: Handle commands teleport, set, and remove.

        return true;
    }

    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args
    ) {
        List<String> completions = new ArrayList<>();

        if (!(sender instanceof Player)) {
            return completions;
        }

        // TODO: Add tab completions.

        return completions;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (
                event.getFrom().getBlockX() != event.getTo().getBlockX() ||
                        event.getFrom().getBlockY() != event.getTo().getBlockY() ||
                        event.getFrom().getBlockZ() != event.getTo().getBlockZ()
        ) {
            Player player = event.getPlayer();
            UUID playerUuid = player.getUniqueId();
            Integer teleportTaskId = this.teleportingPlayers.get(playerUuid);

            if (teleportTaskId != null) {
                // Remove from global teleporting list.
                this.globalTeleportingPlayers.remove(player.getUniqueId());
                // Remove from Map.
                this.teleportingPlayers.remove(playerUuid);
                // Cancel Bukkit Runnable.
                Bukkit.getScheduler().cancelTask(teleportTaskId);

                player.sendMessage(Component
                        .text(HomeTeleportMessage.ERROR_TELEPORTATION_CANCELLED.getMessage())
                        .color(NamedTextColor.RED)
                );
            }
        }
    }

}
