package net.minedo.mc.functionalities.customcommand.teleport.region;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minedo.mc.Minedo;
import net.minedo.mc.constants.command.message.globalteleportmessage.GlobalTeleportMessage;
import net.minedo.mc.constants.command.message.regionteleportmessage.RegionTeleportMessage;
import net.minedo.mc.constants.common.Common;
import net.minedo.mc.constants.feedbacksound.FeedbackSound;
import net.minedo.mc.functionalities.utils.PlayerUtils;
import net.minedo.mc.models.region.Region;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Region teleport.
 */
public class RegionTeleport implements CommandExecutor, Listener, TabCompleter {

    private final Region region;
    private final List<Region> regions;
    private final List<UUID> globalTeleportingPlayers;
    private final HashMap<UUID, Integer> teleportingPlayers = new HashMap<>();

    /**
     * Initialize region teleport.
     *
     * @param region                   region
     * @param regions                  list of all regions
     * @param globalTeleportingPlayers list of globally teleporting players
     */
    public RegionTeleport(
            @NotNull Region region, @NotNull List<Region> regions, @NotNull List<UUID> globalTeleportingPlayers
    ) {
        this.region = region;
        this.regions = regions;
        this.globalTeleportingPlayers = globalTeleportingPlayers;
    }

    /**
     * Get whether command is valid.
     *
     * @param args arguments
     * @return whether command is valid
     */

    private boolean isCommandValid(@NotNull String[] args) {
        if (args.length != 1) {
            return false;
        }

        String warpDestination = args[0];

        return this.regions.stream().anyMatch(region -> region.name().toLowerCase().equals(warpDestination));
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args
    ) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (!this.isCommandValid(args)) {
            player.sendMessage(Component
                    .text(String.format(RegionTeleportMessage.ERROR_USAGE.getMessage()))
                    .color(NamedTextColor.GRAY)
            );

            return true;
        }

        if (
                globalTeleportingPlayers.contains(player.getUniqueId())
                        || teleportingPlayers.containsKey(player.getUniqueId())
        ) {
            player.sendMessage(Component
                    .text(GlobalTeleportMessage.ERROR_USE_MORE_THAN_ONCE_AT_A_TIME.getMessage())
                    .color(NamedTextColor.RED)
            );

            return true;
        }

        String warpDestination = args[0];
        long DURATION = 1;
        long DELAY = 1;
        int teleportTaskId = new RegionTeleportScheduler(
                player, region, this.globalTeleportingPlayers,
                this.teleportingPlayers
        ).runTaskTimer(
                Minedo.getInstance(),
                DELAY * (int) Common.TICK_PER_SECOND.getValue(),
                DURATION * (int) Common.TICK_PER_SECOND.getValue()
        ).getTaskId();

        this.globalTeleportingPlayers.add(player.getUniqueId());
        this.teleportingPlayers.put(player.getUniqueId(), teleportTaskId);

        player.sendMessage(Component.text(
                String.format(RegionTeleportMessage.INFO_TELEPORTING.getMessage(), warpDestination)
        ).color(NamedTextColor.YELLOW));

        return true;
    }

    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args
    ) {
        List<String> completions = new ArrayList<>();

        if (!(sender instanceof Player)) {
            return completions;
        }

        if (args.length == 1) {
            completions.addAll(this.regions.stream().map(region -> region.name().toLowerCase()).toList());
        }

        return completions;
    }

    @EventHandler
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        if (!PlayerUtils.isPlayerMoving(event)) {
            return;
        }

        Player player = event.getPlayer();
        UUID playerUuid = player.getUniqueId();
        Integer teleportTaskId = this.teleportingPlayers.get(playerUuid);

        if (teleportTaskId != null) {
            // Remove from global teleporting list.
            this.globalTeleportingPlayers.remove(player.getUniqueId());
            // Remove from Map.
            this.teleportingPlayers.remove(playerUuid);
            // Cancel Bukkit Runnable.
            Bukkit.getScheduler().cancelTask(teleportTaskId);

            FeedbackSound feedbackSound = FeedbackSound.ERROR;

            player.playSound(player.getLocation(), feedbackSound.getSound(),
                    feedbackSound.getVolume(), feedbackSound.getPitch());
            player.sendMessage(Component
                    .text(RegionTeleportMessage.ERROR_TELEPORTATION_CANCELLED.getMessage())
                    .color(NamedTextColor.RED)
            );
        }
    }

}
