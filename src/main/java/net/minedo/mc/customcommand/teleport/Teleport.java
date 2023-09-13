package net.minedo.mc.customcommand.teleport;

import net.minedo.mc.Minedo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class Teleport implements CommandExecutor, Listener {

    private final double destinationMinX;
    private final double destinationMaxX;
    private final double destinationMinZ;
    private final double destinationMaxZ;
    private final String customCommand;
    private final World world;
    private final Minedo pluginInstance;
    private final Map<UUID, Integer> teleportingPlayers = new HashMap<>();

    public Teleport(
            double destinationMinX, double destinationMaxX, double destinationMinZ, double destinationMaxZ,
            String customCommand, World world, Minedo pluginInstance
    ) {
        this.destinationMinX = destinationMinX;
        this.destinationMaxX = destinationMaxX;
        this.destinationMinZ = destinationMinZ;
        this.destinationMaxZ = destinationMaxZ;
        this.customCommand = customCommand;
        this.world = world;
        this.pluginInstance = pluginInstance;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args
    ) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (teleportingPlayers.containsKey(((Player) sender).getUniqueId())) {
            player.sendMessage(Component.text("You're already teleporting!").color(NamedTextColor.RED));

            return true;
        }

        if (label.equalsIgnoreCase(customCommand)) {
            // Set random location within region.
            Random random = new Random();
            double coordinateX = random.nextInt((int) (this.destinationMaxX - this.destinationMinX + 1)) + this.destinationMinX;
            double coordinateZ = random.nextInt((int) (this.destinationMaxZ - this.destinationMinZ + 1)) + this.destinationMinZ;
            double coordinateY = this.world.getHighestBlockYAt((int) coordinateX, (int) coordinateZ);
            Location location = new Location(this.world, coordinateX, coordinateY, coordinateZ);

            int teleportTaskId = new TeleportScheduler(player, location, this.customCommand, this.teleportingPlayers)
                    .runTaskTimer(this.pluginInstance, 20, 20)
                    .getTaskId();

            this.teleportingPlayers.put(player.getUniqueId(), teleportTaskId);
            player.sendMessage(Component.text(
                    String.format("Teleporting to %s in 5..", customCommand)
            ).color(NamedTextColor.YELLOW));

            return true;
        }

        return false;
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
                // Remove from Map.
                this.teleportingPlayers.remove(playerUuid);
                // Cancel Bukkit Runnable.
                Bukkit.getScheduler().cancelTask(teleportTaskId);

                player.sendMessage(
                        Component.text("Don't move if you want to teleport..").color(NamedTextColor.RED)
                );
            }
        }
    }

}
