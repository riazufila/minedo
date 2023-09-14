package net.minedo.mc.customcommand.teleport.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class PlayerTeleportScheduler extends BukkitRunnable {

    private final Player teleportingPlayer;
    private final Player stillPlayer;
    private final Map<UUID, Integer> teleportingRequesters;
    private final Map<UUID, Integer> standingStillRequestees;
    private int countdown;

    public PlayerTeleportScheduler(
            Player teleportingPlayer, Player stillPlayer,
            Map<UUID, Integer> teleportingRequesters, Map<UUID, Integer> standingStillRequestees
    ) {
        this.countdown = 4;
        this.teleportingPlayer = teleportingPlayer;
        this.stillPlayer = stillPlayer;
        this.teleportingRequesters = teleportingRequesters;
        this.standingStillRequestees = standingStillRequestees;
    }

    @Override
    public void run() {
        if (countdown > 0) {
            teleportingPlayer.sendMessage(Component.text(String.format("%s..", countdown)).color(NamedTextColor.YELLOW));
            countdown--;
        } else {
            if (teleportingPlayer.isOnline() && stillPlayer.isOnline()) {
                teleportingPlayer.teleport(this.stillPlayer);

                teleportingPlayer.sendMessage(Component
                        .text(String.format("Teleported to %s!", this.stillPlayer.getName()))
                        .color(NamedTextColor.GREEN)
                );
                stillPlayer.sendMessage(Component
                        .text(String.format("%s teleported to you!", this.teleportingPlayer.getName()))
                        .color(NamedTextColor.GREEN)
                );
            }

            teleportingRequesters.remove(teleportingPlayer.getUniqueId());
            standingStillRequestees.remove(stillPlayer.getUniqueId());
            this.cancel();
        }
    }

}
