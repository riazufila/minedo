package net.minedo.mc.customcommand.teleport.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minedo.mc.Minedo;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PlayerTeleport implements CommandExecutor, Listener {

    private final Minedo pluginInstance;
    private final Map<UUID, Integer> teleportRequestRequesters = new HashMap<>();
    private final Map<UUID, Integer> teleportRequestRequestees = new HashMap<>();
    private final Map<UUID, Integer> teleportingRequesters = new HashMap<>();
    private final Map<UUID, Integer> standingStillRequestees = new HashMap<>();

    public PlayerTeleport(Minedo pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    private boolean isCommandValid(String[] args) {
        if (args.length == 0) {
            return false;
        }

        String teleportType = args[0];

        if (teleportType.equals("request")) {
            return args.length == 2;
        }

        if (teleportType.equals("accept") || teleportType.equals("decline")) {
            return args.length == 1;
        }

        return false;
    }

    private UUID getOtherPlayerUuidFromPlayer(Integer teleportRequestTaskId) {
        UUID otherPlayerUuid = null;

        for (Map.Entry<UUID, Integer> entry : teleportRequestRequesters.entrySet()) {
            if (entry.getValue().equals(teleportRequestTaskId)) {
                otherPlayerUuid = entry.getKey();
            }
        }

        return otherPlayerUuid;
    }

    private Player removePlayersFromRequestQueue(Player player, UUID otherPlayerUuid, Integer teleportRequestTaskId) {
        Player otherPlayer = this.pluginInstance.getServer().getPlayer(Objects.requireNonNull(otherPlayerUuid));

        this.teleportRequestRequesters.remove(otherPlayerUuid);
        this.teleportRequestRequestees.remove(player.getUniqueId());

        Bukkit.getScheduler().cancelTask(teleportRequestTaskId);

        return otherPlayer;
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
                    .text("Usage: /teleport <request <player> | accept | decline>")
                    .color(NamedTextColor.GRAY)
            );

            return true;
        }

        String teleportType = args[0];

        switch (teleportType) {
            case "request" -> {
                String teleportTarget = args[1];
                Player otherPlayer = this.pluginInstance.getServer().getPlayer(teleportTarget);
                Integer existingTeleportRequestTaskId = teleportRequestRequesters.get(player.getUniqueId());

                if (existingTeleportRequestTaskId != null) {
                    player.sendMessage(Component
                            .text("You already have a teleport request.")
                            .color(NamedTextColor.RED)
                    );
                    return true;
                }

                if (otherPlayer != null && otherPlayer.isOnline()) {
                    int teleportRequestTaskId = new PlayerTeleportRequestTimer(player, otherPlayer, teleportRequestRequesters, teleportRequestRequestees)
                            .runTaskLater(this.pluginInstance, 600)
                            .getTaskId();

                    this.teleportRequestRequesters.put(player.getUniqueId(), teleportRequestTaskId);
                    this.teleportRequestRequestees.put(otherPlayer.getUniqueId(), teleportRequestTaskId);

                    otherPlayer.sendMessage(Component
                            .text(String.format("%s is requesting to teleport..", player.getName()))
                            .color(NamedTextColor.YELLOW)
                    );
                } else {
                    player.sendMessage(Component
                            .text(String.format("%s is not in the server.", teleportTarget))
                            .color(NamedTextColor.RED)
                    );
                }

                return true;
            }

            case "accept" -> {
                Integer existingTeleportRequestTaskId = teleportRequestRequestees.get(player.getUniqueId());
                Integer existingTeleportingTaskId = teleportingRequesters.get(player.getUniqueId());
                Integer existingStandingStillTaskId = standingStillRequestees.get(player.getUniqueId());

                if (existingTeleportRequestTaskId == null) {
                    player.sendMessage(Component
                            .text("You don't have a teleport request.")
                            .color(NamedTextColor.RED)
                    );

                    return true;
                }

                if (existingTeleportingTaskId != null || existingStandingStillTaskId != null) {
                    player.sendMessage(Component
                            .text("You're already in a teleportation process.")
                            .color(NamedTextColor.RED)
                    );

                    return true;
                }

                UUID otherPlayerUuid = this.getOtherPlayerUuidFromPlayer(existingTeleportRequestTaskId);
                Player otherPlayer = this.removePlayersFromRequestQueue(player, otherPlayerUuid, existingTeleportRequestTaskId);

                if (otherPlayer != null && otherPlayer.isOnline()) {
                    int teleportingTaskId = new PlayerTeleportScheduler(otherPlayer, player, teleportingRequesters, standingStillRequestees)
                            .runTaskTimer(this.pluginInstance, 20, 20)
                            .getTaskId();

                    this.teleportingRequesters.put(otherPlayer.getUniqueId(), teleportingTaskId);
                    this.standingStillRequestees.put(player.getUniqueId(), teleportingTaskId);

                    player.sendMessage(Component
                            .text(String.format("Stand still while %s is teleporting..", otherPlayer.getName()))
                            .color(NamedTextColor.YELLOW)
                    );
                    otherPlayer.sendMessage(Component
                            .text(String.format("Teleporting to %s in 5..", player.getName()))
                            .color(NamedTextColor.YELLOW)
                    );
                }

                return true;
            }

            case "decline" -> {
                Integer existingTeleportRequestTaskId = teleportRequestRequestees.get(player.getUniqueId());

                if (existingTeleportRequestTaskId == null) {
                    player.sendMessage(Component
                            .text("You don't have a teleport request.")
                            .color(NamedTextColor.RED)
                    );

                    return true;
                }

                UUID otherPlayerUuid = this.getOtherPlayerUuidFromPlayer(existingTeleportRequestTaskId);
                Player otherPlayer = this.removePlayersFromRequestQueue(player, otherPlayerUuid, existingTeleportRequestTaskId);

                if (otherPlayer != null && otherPlayer.isOnline()) {
                    otherPlayer.sendMessage(Component
                            .text(String.format("%s declined your teleport request.", player.getName()))
                            .color(NamedTextColor.RED)
                    );
                    player.sendMessage(Component
                            .text(String.format("You declined %s request to teleport.", otherPlayer.getName()))
                            .color(NamedTextColor.RED)
                    );
                } else {
                    OfflinePlayer otherOfflinePlayer = this.pluginInstance.getServer().getOfflinePlayer(otherPlayerUuid);

                    player.sendMessage(Component
                            .text(String.format("You declined %s request to teleport.", otherOfflinePlayer.getName()))
                            .color(NamedTextColor.RED)
                    );
                }

                return true;
            }

            default -> {
                player.sendMessage(Component
                        .text("Usage: /teleport <request <player> | accept | decline>")
                        .color(NamedTextColor.GRAY)
                );
                return true;
            }
        }
    }

    private void handleTeleportCancellation(
            Player player, Integer teleportTaskId, Map<UUID, Integer> teleportingPlayer, Boolean isRequester
    ) {
        Player otherPlayer = null;
        UUID playerUuid = player.getUniqueId();
        UUID otherPlayerUuid = null;

        // Get other player's UUID.
        for (Map.Entry<UUID, Integer> entry : teleportingPlayer.entrySet()) {
            if (Objects.equals(entry.getValue(), teleportTaskId)) {
                otherPlayerUuid = entry.getKey();
                otherPlayer = this.pluginInstance.getServer().getPlayer(otherPlayerUuid);
            }
        }

        // Remove from Map.
        if (isRequester) {
            this.teleportingRequesters.remove(playerUuid);
            this.standingStillRequestees.remove(otherPlayerUuid);
        } else {
            this.teleportingRequesters.remove(otherPlayerUuid);
            this.standingStillRequestees.remove(playerUuid);
        }

        // Cancel Bukkit Runnable.
        Bukkit.getScheduler().cancelTask(teleportTaskId);

        this.sendTeleportationCancelled(player);
        this.sendTeleportationCancelled(otherPlayer);
    }

    private void sendTeleportationCancelled(Player player) {
        if (player != null && player.isOnline()) {
            player.sendMessage(
                    Component.text("Teleportation cancelled.").color(NamedTextColor.RED)
            );
        }
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
            Integer requesterTeleportTaskId = this.teleportingRequesters.get(playerUuid);
            Integer requesteeTeleportTaskId = this.standingStillRequestees.get(playerUuid);

            if (requesterTeleportTaskId != null) {
                this.handleTeleportCancellation(player, requesterTeleportTaskId, standingStillRequestees, true);
            } else if (requesteeTeleportTaskId != null) {
                this.handleTeleportCancellation(player, requesteeTeleportTaskId, teleportingRequesters, false);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Integer existingTeleportRequestTaskId = teleportRequestRequestees.get(player.getUniqueId());

        if (existingTeleportRequestTaskId == null) {
            return;
        }

        UUID otherPlayerUuid = this.getOtherPlayerUuidFromPlayer(existingTeleportRequestTaskId);
        Player otherPlayer = this.removePlayersFromRequestQueue(player, otherPlayerUuid, existingTeleportRequestTaskId);
    }

}
