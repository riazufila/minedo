package net.minedo.mc.customcommand.teleport.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerTeleportScheduler extends BukkitRunnable {

    private final Player player;
    private final Player otherPlayer;
    private final Map<UUID, Integer> teleportingRequesters;
    private final Map<UUID, Integer> standingStillRequestees;
    private int countdown;

    public PlayerTeleportScheduler(
            Player player, Player otherPlayer,
            Map<UUID, Integer> teleportingRequesters, Map<UUID, Integer> standingStillRequestees
    ) {
        this.countdown = 4;
        this.player = player;
        this.otherPlayer = otherPlayer;
        this.teleportingRequesters = teleportingRequesters;
        this.standingStillRequestees = standingStillRequestees;
    }

    @Override
    public void run() {
        if (countdown > 0) {
            player.sendMessage(Component.text(String.format("%s..", countdown)).color(NamedTextColor.YELLOW));
            countdown--;
        } else {
            if (player.isOnline() && otherPlayer.isOnline()) {
                player.teleport(this.otherPlayer);

                player.sendMessage(Component.text(
                        String.format("Teleported to %s!", this.otherPlayer.getName())
                ).color(NamedTextColor.GREEN));
                otherPlayer.sendMessage(Component.text(
                        String.format("%s teleported to you!", this.player.getName())
                ));
            }

            teleportingRequesters.remove(player.getUniqueId());
            standingStillRequestees.remove(otherPlayer.getUniqueId());
            this.cancel();
        }
    }

}
