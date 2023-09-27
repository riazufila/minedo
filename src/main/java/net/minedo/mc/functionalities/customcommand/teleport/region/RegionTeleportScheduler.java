package net.minedo.mc.functionalities.customcommand.teleport.region;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minedo.mc.constants.regionteleportmessage.RegionTeleportMessage;
import net.minedo.mc.models.region.Region;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RegionTeleportScheduler extends BukkitRunnable {

    private final Player player;
    private final Region region;
    private final List<UUID> globalTeleportingPlayers;
    private final HashMap<UUID, Integer> teleportingPlayers;
    private int countdown = 4;

    public RegionTeleportScheduler(
            Player player, Region region, List<UUID> globalTeleportingPlayers,
            HashMap<UUID, Integer> teleportingPlayers
    ) {
        this.player = player;
        this.region = region;
        this.globalTeleportingPlayers = globalTeleportingPlayers;
        this.teleportingPlayers = teleportingPlayers;
    }

    private Location getSafeToTeleportLocation(Location location) {
        World world = this.region.getWorldType();

        return new Location(
                world, location.getX() + 0.5, location.getY() + 1, location.getZ() + 0.5
        );
    }

    @Override
    public void run() {
        Location location = this.getSafeToTeleportLocation(this.region.getRandomLocation());
        World sourceWorld = player.getWorld();
        World destinationWorld = this.region.getWorldType();

        if (countdown > 0) {
            player.sendMessage(Component
                    .text(String.format(RegionTeleportMessage.INFO_COUNTDOWN.getMessage(), countdown))
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
                                RegionTeleportMessage.SUCCESS_TELEPORT.getMessage(),
                                this.region.getName().toLowerCase()
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
