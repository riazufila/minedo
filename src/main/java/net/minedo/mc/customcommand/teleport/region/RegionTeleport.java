package net.minedo.mc.customcommand.teleport.region;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minedo.mc.Minedo;
import net.minedo.mc.constants.globalteleportmessage.GlobalTeleportMessage;
import net.minedo.mc.constants.regionteleportmessage.RegionTeleportMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RegionTeleport implements CommandExecutor, Listener, TabCompleter {

    private final double destinationMinX;
    private final double destinationMaxX;
    private final double destinationMinZ;
    private final double destinationMaxZ;
    private final String customCommand;
    private final List<UUID> globalTeleportingPlayers;
    private final World world;
    private final Minedo pluginInstance;
    private final Map<UUID, Integer> teleportingPlayers = new HashMap<>();

    public RegionTeleport(
            double destinationMinX, double destinationMaxX, double destinationMinZ, double destinationMaxZ,
            String customCommand, List<UUID> globalTeleportingPlayers, World world, Minedo pluginInstance
    ) {
        this.destinationMinX = destinationMinX;
        this.destinationMaxX = destinationMaxX;
        this.destinationMinZ = destinationMinZ;
        this.destinationMaxZ = destinationMaxZ;
        this.customCommand = customCommand;
        this.globalTeleportingPlayers = globalTeleportingPlayers;
        this.world = world;
        this.pluginInstance = pluginInstance;
    }

    private Location getSafeToTeleportCoordinate(double coordinateX, double coordinateY, double coordinateZ) {
        return new Location(this.world, coordinateX + 0.5, coordinateY + 1, coordinateZ + 0.5);
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args
    ) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length > 0) {
            player.sendMessage(Component
                    .text(String.format(RegionTeleportMessage.ERROR_USAGE.getMessage(), this.customCommand))
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

        if (label.equalsIgnoreCase(customCommand)) {
            // Set random location within region.
            Random random = new Random();

            int coordinateX = random.nextInt(
                    (int) ((this.destinationMaxX - this.destinationMinX + 1) + this.destinationMinX)
            );
            int coordinateZ = random.nextInt(
                    (int) ((this.destinationMaxZ - this.destinationMinZ + 1) + this.destinationMinZ)
            );

            int coordinateY = this.world.getHighestBlockYAt(coordinateX, coordinateZ);
            Location location = this.getSafeToTeleportCoordinate(coordinateX, coordinateY, coordinateZ);

            int teleportTaskId = new RegionTeleportScheduler(
                    player, location, this.customCommand,
                    this.globalTeleportingPlayers, this.teleportingPlayers, this.world
            ).runTaskTimer(this.pluginInstance, 20, 20).getTaskId();

            this.globalTeleportingPlayers.add(player.getUniqueId());
            this.teleportingPlayers.put(player.getUniqueId(), teleportTaskId);
            player.sendMessage(Component.text(
                    String.format(RegionTeleportMessage.INFO_TELEPORTING.getMessage(), customCommand)
            ).color(NamedTextColor.YELLOW));

            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args
    ) {
        return Collections.emptyList();
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
                        .text(RegionTeleportMessage.ERROR_TELEPORTATION_CANCELLED.getMessage())
                        .color(NamedTextColor.RED)
                );
            }
        }
    }

}
