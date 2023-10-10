package net.minedo.mc.functionalities.customcommand.teleport.home;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minedo.mc.constants.command.feedbacksound.FeedbackSound;
import net.minedo.mc.constants.command.message.hometeleportmessage.HomeTeleportMessage;
import net.minedo.mc.models.playerhome.PlayerHome;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Home teleport scheduler.
 */
public class HomeTeleportScheduler extends BukkitRunnable {

    private final Player player;
    private final PlayerHome home;
    private final List<UUID> globalTeleportingPlayers;
    private final HashMap<UUID, Integer> teleportingPlayers;
    private int countDown = 4;

    /**
     * Initialize home teleport.
     *
     * @param player                   player
     * @param home                     player home
     * @param globalTeleportingPlayers list of globally teleporting players
     * @param teleportingPlayers       players teleporting to home
     */
    public HomeTeleportScheduler(
            Player player, PlayerHome home, List<UUID> globalTeleportingPlayers,
            HashMap<UUID, Integer> teleportingPlayers
    ) {
        this.player = player;
        this.home = home;
        this.globalTeleportingPlayers = globalTeleportingPlayers;
        this.teleportingPlayers = teleportingPlayers;
    }

    @Override
    public void run() {
        Location location = new Location(
                home.worldType(), home.coordinateX(), home.coordinateY(), home.coordinateZ()
        );

        World sourceWorld = player.getWorld();
        World destinationWorld = home.worldType();

        if (countDown > 0) {
            player.sendMessage(Component
                    .text(String.format(HomeTeleportMessage.INFO_COUNTDOWN.getMessage(), countDown))
                    .color(NamedTextColor.YELLOW)
            );

            countDown--;
        } else {
            if (player.isOnline()) {
                sourceWorld.playSound(player.getLocation(), FeedbackSound.TELEPORT.getSound(), 1, 1);

                player.teleport(location);
                destinationWorld.playSound(location, FeedbackSound.TELEPORT.getSound(), 1, 1);

                player.sendMessage(Component
                        .text(String.format(
                                HomeTeleportMessage.SUCCESS_TELEPORT.getMessage(),
                                home.name()
                        ))
                        .color(NamedTextColor.GREEN)
                );
            }

            this.globalTeleportingPlayers.remove(player.getUniqueId());
            this.teleportingPlayers.remove(player.getUniqueId());

            this.cancel();
        }
    }

}
