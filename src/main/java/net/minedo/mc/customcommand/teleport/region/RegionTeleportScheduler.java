package net.minedo.mc.customcommand.teleport.region;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RegionTeleportScheduler extends BukkitRunnable {

    private final Player player;
    private final Location destination;
    private final String customCommand;
    private final List<UUID> globalTeleportingPlayers;
    private final Map<UUID, Integer> teleportingPlayers;
    private int countdown;

    public RegionTeleportScheduler(
            Player player, Location destination, String customCommand,
            List<UUID> globalTeleportingPlayers, Map<UUID, Integer> teleportingPlayers
    ) {
        this.countdown = 4;
        this.player = player;
        this.destination = destination;
        this.customCommand = customCommand;
        this.globalTeleportingPlayers = globalTeleportingPlayers;
        this.teleportingPlayers = teleportingPlayers;
    }

    @Override
    public void run() {
        if (countdown > 0) {
            player.sendMessage(Component.text(String.format("%s..", countdown)).color(NamedTextColor.YELLOW));
            countdown--;
        } else {
            if (player.isOnline()) {
                player.teleport(this.destination);

                player.sendMessage(Component
                        .text(String.format("Teleported to %s!", this.customCommand))
                        .color(NamedTextColor.GREEN)
                );
            }

            this.globalTeleportingPlayers.remove(player.getUniqueId());
            this.teleportingPlayers.remove(player.getUniqueId());

            this.cancel();
        }
    }

}
