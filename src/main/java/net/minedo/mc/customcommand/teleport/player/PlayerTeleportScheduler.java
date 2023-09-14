package net.minedo.mc.customcommand.teleport.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class PlayerTeleportScheduler extends BukkitRunnable {

    private final Player player;
    private final Location destination;
    private final String customCommand;
    private final Map<UUID, Integer> teleportingPlayers;
    private int countdown;

    public PlayerTeleportScheduler(
            Player player, Location destination, String customCommand, Map<UUID, Integer> teleportingPlayers
    ) {
        this.countdown = 4;
        this.player = player;
        this.destination = destination;
        this.customCommand = customCommand;
        this.teleportingPlayers = teleportingPlayers;
    }

    @Override
    public void run() {
        if (countdown > 0) {
            player.sendMessage(Component.text(String.format("%s..", countdown)).color(NamedTextColor.YELLOW));
            countdown--;
        } else {
            // Check if the player is still online and has not moved
            if (player.isOnline()) {
                // Teleport the player to the spawn point
                player.teleport(this.destination);
                player.sendMessage(Component.text(
                        String.format("Teleported to %s!", this.customCommand)
                ).color(NamedTextColor.GREEN));
            }

            teleportingPlayers.remove(player.getUniqueId());
            this.cancel();
        }
    }

}