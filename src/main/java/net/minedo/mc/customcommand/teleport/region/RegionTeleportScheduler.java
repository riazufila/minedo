package net.minedo.mc.customcommand.teleport.region;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RegionTeleportScheduler extends BukkitRunnable {

    private final Player player;
    private final Location destination;
    private final String customCommand;
    private final List<UUID> globalTeleportingPlayers;
    private final Map<UUID, Integer> teleportingPlayers;
    private final World world;
    private int countdown;

    public RegionTeleportScheduler(
            Player player, Location destination, String customCommand,
            List<UUID> globalTeleportingPlayers, Map<UUID, Integer> teleportingPlayers, World world
    ) {
        this.countdown = 4;
        this.player = player;
        this.destination = destination;
        this.customCommand = customCommand;
        this.globalTeleportingPlayers = globalTeleportingPlayers;
        this.teleportingPlayers = teleportingPlayers;
        this.world = world;
    }

    @Override
    public void run() {
        if (countdown > 0) {
            player.sendMessage(Component.text(String.format("%s..", countdown)).color(NamedTextColor.YELLOW));
            countdown--;
        } else {
            if (player.isOnline()) {
                player.teleport(this.destination);
                world.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);

                player.sendMessage(Component
                        .text(String.format("Teleported to %s!", this.customCommand))
                        .color(NamedTextColor.GREEN)
                );
            }

            this.globalTeleportingPlayers.remove(player.getUniqueId());
            this.teleportingPlayers.remove(player.getUniqueId());

            this.cancel();
        }
    }

}
