package net.minedo.mc.customcommand.teleport.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minedo.mc.constants.playerteleportmessage.PlayerTeleportMessage;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class PlayerTeleportRequestTimer extends BukkitRunnable {

    private final Player requester;
    private final Player requestee;
    private final Map<UUID, Integer> teleportRequesters;
    private final Map<UUID, Integer> teleportRequestees;

    public PlayerTeleportRequestTimer(
            Player requester, Player requestee,
            Map<UUID, Integer> teleportRequesters, Map<UUID, Integer> teleportRequestees
    ) {
        this.requester = requester;
        this.requestee = requestee;
        this.teleportRequesters = teleportRequesters;
        this.teleportRequestees = teleportRequestees;
    }

    @Override
    public void run() {
        teleportRequesters.remove(this.requester.getUniqueId());
        teleportRequestees.remove(this.requestee.getUniqueId());

        this.cancel();

        Component timeoutMessage = Component
                .text(PlayerTeleportMessage.ERROR_REQUEST_TIMEOUT.getMessage())
                .color(NamedTextColor.RED);

        requester.sendMessage(timeoutMessage);
        requestee.sendMessage(timeoutMessage);
    }

}
