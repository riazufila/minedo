package net.minedo.mc.functionalities.customcommand.teleport.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minedo.mc.constants.command.feedbacksound.FeedbackSound;
import net.minedo.mc.constants.command.message.playerteleportmessage.PlayerTeleportMessage;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

/**
 * Player teleport request scheduler.
 */
public class PlayerTeleportRequestScheduler extends BukkitRunnable {

    private final Player requester;
    private final Player requestee;
    private final HashMap<UUID, Integer> teleportRequesters;
    private final HashMap<UUID, Integer> teleportRequestees;

    /**
     * Initialize player teleport request scheduler.
     *
     * @param requester          requester
     * @param requestee          requestee
     * @param teleportRequesters teleport requesters
     * @param teleportRequestees teleport requestees
     */
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

        requester.playSound(requester.getLocation(), FeedbackSound.ERROR.getSound(), 1, 1);
        requester.sendMessage(timeoutMessage);

        requestee.playSound(requestee.getLocation(), FeedbackSound.ERROR.getSound(), 1, 1);
        requestee.sendMessage(timeoutMessage);
    }

}
