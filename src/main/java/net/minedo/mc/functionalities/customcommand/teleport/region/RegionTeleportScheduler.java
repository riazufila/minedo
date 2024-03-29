package net.minedo.mc.functionalities.customcommand.teleport.region;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minedo.mc.constants.command.message.regionteleportmessage.RegionTeleportMessage;
import net.minedo.mc.constants.feedbacksound.FeedbackSound;
import net.minedo.mc.models.region.Region;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Region teleport scheduler.
 */
public class RegionTeleportScheduler extends BukkitRunnable {

    private final Player player;
    private final Region region;
    private final List<UUID> globalTeleportingPlayers;
    private final HashMap<UUID, Integer> teleportingPlayers;
    private int countDown = 4;

    /**
     * Initialize region teleport scheduler.
     *
     * @param player                   player
     * @param region                   region where player is being teleported to
     * @param globalTeleportingPlayers list of globally teleporting players
     * @param teleportingPlayers       players teleporting to region
     */
    public RegionTeleportScheduler(
            @NotNull Player player,
            @NotNull Region region,
            @NotNull List<UUID> globalTeleportingPlayers,
            @NotNull HashMap<UUID, Integer> teleportingPlayers
    ) {
        this.player = player;
        this.region = region;
        this.globalTeleportingPlayers = globalTeleportingPlayers;
        this.teleportingPlayers = teleportingPlayers;
    }

    /**
     * Get safe location to teleport to.
     *
     * @param location location
     * @return safe location to teleport to
     */
    private @NotNull Location getSafeToTeleportLocation(@NotNull Location location) {
        World world = this.region.worldType();

        return new Location(
                world, location.getX() + 0.5, location.getY() + 1, location.getZ() + 0.5
        );
    }

    @Override
    public void run() {
        Location location = this.getSafeToTeleportLocation(this.region.getRandomLocation());
        World sourceWorld = player.getWorld();
        World destinationWorld = this.region.worldType();

        if (countDown > 0) {
            player.sendMessage(Component
                    .text(String.format(RegionTeleportMessage.INFO_COUNTDOWN.getMessage(), countDown))
                    .color(NamedTextColor.YELLOW)
            );

            countDown--;
        } else {
            if (player.isOnline() && !player.isDead()) {
                FeedbackSound feedbackSound = FeedbackSound.TELEPORT;
                Sound sound = feedbackSound.getSound();
                float volume = feedbackSound.getVolume();
                float pitch = feedbackSound.getPitch();

                sourceWorld.playSound(player.getLocation(), sound, volume, pitch);

                player.teleport(location);
                destinationWorld.playSound(location, sound, volume, pitch);

                player.sendMessage(Component
                        .text(String.format(
                                RegionTeleportMessage.SUCCESS_TELEPORT.getMessage(),
                                this.region.name().toLowerCase()
                        ))
                        .color(NamedTextColor.GREEN)
                );
            } else {
                player.sendMessage(Component
                        .text(RegionTeleportMessage.ERROR_UNSUITABLE_CONDITION.getMessage())
                        .color(NamedTextColor.RED)
                );
            }

            this.globalTeleportingPlayers.remove(player.getUniqueId());
            this.teleportingPlayers.remove(player.getUniqueId());

            this.cancel();
        }
    }

}
