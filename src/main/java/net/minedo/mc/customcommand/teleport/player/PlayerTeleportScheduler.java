package net.minedo.mc.customcommand.teleport.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerTeleportScheduler extends BukkitRunnable {

    private final Player teleportingPlayer;
    private final Player stillPlayer;
    private final Map<UUID, Integer> teleportingRequesters;
    private final Map<UUID, Integer> standingStillRequestees;
    private final List<UUID> globalTeleportingPlayers;
    private int countdown;
    private final World world;

    public PlayerTeleportScheduler(
            Player teleportingPlayer, Player stillPlayer,
            Map<UUID, Integer> teleportingRequesters, Map<UUID, Integer> standingStillRequestees,
            List<UUID> globalTeleportingPlayers, World world
    ) {
        this.countdown = 4;
        this.teleportingPlayer = teleportingPlayer;
        this.stillPlayer = stillPlayer;
        this.teleportingRequesters = teleportingRequesters;
        this.standingStillRequestees = standingStillRequestees;
        this.globalTeleportingPlayers = globalTeleportingPlayers;
        this.world = world;
    }

    @Override
    public void run() {
        if (countdown > 0) {
            teleportingPlayer.sendMessage(Component.text(String.format("%s..", countdown)).color(NamedTextColor.YELLOW));
            countdown--;
        } else {
            if (teleportingPlayer.isOnline() && stillPlayer.isOnline()) {
                teleportingPlayer.teleport(this.stillPlayer);
                this.world.playSound(teleportingPlayer.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);

                teleportingPlayer.sendMessage(Component
                        .text(String.format("Teleported to %s!", this.stillPlayer.getName()))
                        .color(NamedTextColor.GREEN)
                );
                stillPlayer.sendMessage(Component
                        .text(String.format("%s teleported to you!", this.teleportingPlayer.getName()))
                        .color(NamedTextColor.GREEN)
                );
            } else if (!teleportingPlayer.isOnline()) {
                stillPlayer.sendMessage(Component
                        .text(String.format("%s went offline.", this.teleportingPlayer.getName()))
                        .color(NamedTextColor.RED)
                );
            } else if (!stillPlayer.isOnline()) {
                teleportingPlayer.sendMessage(Component
                        .text(String.format("%s went offline.", this.stillPlayer.getName()))
                        .color(NamedTextColor.RED)
                );
            }

            this.globalTeleportingPlayers.remove(this.teleportingPlayer.getUniqueId());
            this.globalTeleportingPlayers.remove(this.stillPlayer.getUniqueId());

            this.teleportingRequesters.remove(this.teleportingPlayer.getUniqueId());
            this.standingStillRequestees.remove(this.stillPlayer.getUniqueId());

            this.cancel();
        }
    }

}
