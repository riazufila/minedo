package net.minedo.mc.customcommand.spawn;

import net.minedo.mc.Minedo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpawnCommand implements CommandExecutor, Listener {

    private final Minedo pluginInstance;
    private final Map<UUID, Integer> teleportingPlayers = new HashMap<>();

    public SpawnCommand(Minedo pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (teleportingPlayers.containsKey(((Player) sender).getUniqueId())) {
            player.sendMessage(Component.text("You're already teleporting!").color(NamedTextColor.RED));

            return false;
        }

        if (label.equalsIgnoreCase("spawn")) {
            int teleportTaskId = new BukkitRunnable() {
                int countdown = 4;

                @Override
                public void run() {
                    if (countdown > 0) {
                        player.sendMessage(Component.text(String.format("%s..", countdown)).color(NamedTextColor.YELLOW));
                        countdown--;
                    } else {
                        // Check if the player is still online and has not moved
                        if (player.isOnline()) {
                            // Teleport the player to the spawn point
                            player.teleport(player.getWorld().getSpawnLocation());
                            player.sendMessage(Component.text("Teleported to spawn!").color(NamedTextColor.GREEN));
                        }

                        teleportingPlayers.remove(player.getUniqueId());
                        this.cancel();
                    }
                }
            }.runTaskTimer(this.pluginInstance, 20, 20).getTaskId();

            this.teleportingPlayers.put(player.getUniqueId(), teleportTaskId);
            player.sendMessage(Component.text("Teleporting to spawn in 5..").color(NamedTextColor.YELLOW));

            return true;
        }

        return false;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockY() != event.getTo().getBlockY() || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            Player player = event.getPlayer();
            UUID playerUuid = player.getUniqueId();
            Integer teleportTaskId = this.teleportingPlayers.get(playerUuid);

            if (teleportTaskId != null) {
                // Remove from Map.
                this.teleportingPlayers.remove(playerUuid);
                // Cancel Bukkit Runnable.
                Bukkit.getScheduler().cancelTask(teleportTaskId);

                player.sendMessage(Component.text("Don't move if you want to teleport..").color(NamedTextColor.RED));
            }
        }
    }

}
