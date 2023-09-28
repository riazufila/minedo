package net.minedo.mc.functionalities.customcommand.teleport.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minedo.mc.constants.command.message.playerteleportmessage.PlayerTeleportMessage;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerTeleportScheduler extends BukkitRunnable {

    private final Player teleportingPlayer;
    private final Player stillPlayer;
    private final HashMap<UUID, Integer> teleportingRequesters;
    private final HashMap<UUID, Integer> standingStillRequestees;
    private final List<UUID> globalTeleportingPlayers;
    private int countdown;

    public PlayerTeleportScheduler(
            Player teleportingPlayer, Player stillPlayer,
            HashMap<UUID, Integer> teleportingRequesters, HashMap<UUID, Integer> standingStillRequestees,
            List<UUID> globalTeleportingPlayers
    ) {
        this.countdown = 4;
        this.teleportingPlayer = teleportingPlayer;
        this.stillPlayer = stillPlayer;
        this.teleportingRequesters = teleportingRequesters;
        this.standingStillRequestees = standingStillRequestees;
        this.globalTeleportingPlayers = globalTeleportingPlayers;
    }

    @Override
    public void run() {
        World sourceWorld = teleportingPlayer.getWorld();
        World destinationWorld = stillPlayer.getWorld();

        if (countdown > 0) {
            teleportingPlayer.sendMessage(Component
                    .text(String.format(PlayerTeleportMessage.INFO_COUNTDOWN.getMessage(), countdown))
                    .color(NamedTextColor.YELLOW)
            );

            countdown--;
        } else {
            if (teleportingPlayer.isOnline() && stillPlayer.isOnline()) {
                teleportingPlayer.teleport(this.stillPlayer);

                sourceWorld.playSound(
                        teleportingPlayer.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1
                );
                destinationWorld.playSound(
                        teleportingPlayer.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1
                );

                teleportingPlayer.sendMessage(Component
                        .text(String.format(
                                PlayerTeleportMessage.SUCCESS_TELEPORT_REQUESTER.getMessage(),
                                this.stillPlayer.getName()
                        ))
                        .color(NamedTextColor.GREEN)
                );
                stillPlayer.sendMessage(Component
                        .text(String.format(
                                PlayerTeleportMessage.SUCCESS_TELEPORT_REQUESTEE.getMessage(),
                                this.teleportingPlayer.getName()
                        ))
                        .color(NamedTextColor.GREEN)
                );
            } else if (!teleportingPlayer.isOnline()) {
                stillPlayer.sendMessage(Component
                        .text(String.format(
                                PlayerTeleportMessage.ERROR_TELEPORT_TARGET_OFFLINE.getMessage(),
                                this.teleportingPlayer.getName()
                        ))
                        .color(NamedTextColor.RED)
                );
            } else if (!stillPlayer.isOnline()) {
                teleportingPlayer.sendMessage(Component
                        .text(String.format(
                                PlayerTeleportMessage.ERROR_TELEPORT_TARGET_OFFLINE.getMessage(),
                                this.stillPlayer.getName()
                        ))
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
