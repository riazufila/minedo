package net.minedo.mc.functionalities.customcommand.teleport.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minedo.mc.constants.command.message.playerteleportmessage.PlayerTeleportMessage;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class PlayerTeleportRequestScheduler extends BukkitRunnable {

    private final Player requester;
    private final Player requestee;
    private final HashMap<UUID, Integer> teleportRequesters;
    private final HashMap<UUID, Integer> teleportRequestees;

    public PlayerTeleportRequestScheduler(
            Player requester, Player requestee,
            HashMap<UUID, Integer> teleportRequesters, HashMap<UUID, Integer> teleportRequestees
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
