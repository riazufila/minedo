package net.minedo.mc.functionalities.customcommand.teleport.home;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minedo.mc.constants.command.message.hometeleportmessage.HomeTeleportMessage;
import net.minedo.mc.models.playerhome.PlayerHome;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class HomeTeleportScheduler extends BukkitRunnable {

    private final Player player;
    private final PlayerHome home;
    private final List<UUID> globalTeleportingPlayers;
    private final HashMap<UUID, Integer> teleportingPlayers;
    private int countdown = 4;

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
                home.getWorldType(), home.getCoordinateX(), home.getCoordinateY(), home.getCoordinateZ()
        );

        World sourceWorld = player.getWorld();
        World destinationWorld = home.getWorldType();

        if (countdown > 0) {
            player.sendMessage(Component
                    .text(String.format(HomeTeleportMessage.INFO_COUNTDOWN.getMessage(), countdown))
                    .color(NamedTextColor.YELLOW)
            );

            countdown--;
        } else {
            if (player.isOnline()) {
                player.teleport(location);

                sourceWorld.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                destinationWorld.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);

                player.sendMessage(Component
                        .text(String.format(
                                HomeTeleportMessage.SUCCESS_TELEPORT.getMessage(),
                                home.getName()
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
