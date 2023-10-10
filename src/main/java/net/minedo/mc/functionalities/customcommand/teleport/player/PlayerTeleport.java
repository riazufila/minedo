package net.minedo.mc.functionalities.customcommand.teleport.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minedo.mc.Minedo;
import net.minedo.mc.constants.command.feedbacksound.FeedbackSound;
import net.minedo.mc.constants.command.message.globalteleportmessage.GlobalTeleportMessage;
import net.minedo.mc.constants.command.message.ignoremessage.IgnoreMessage;
import net.minedo.mc.constants.command.message.playerteleportmessage.PlayerTeleportMessage;
import net.minedo.mc.constants.command.type.playerteleporttype.PlayerTeleportType;
import net.minedo.mc.constants.common.Common;
import net.minedo.mc.repositories.playerblockedlistrepository.PlayerBlockedRepository;
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
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Player teleport.
 */
public class PlayerTeleport implements CommandExecutor, Listener, TabCompleter {

    private final List<UUID> globalTeleportingPlayers;
    private final HashMap<UUID, Integer> teleportRequestRequesters = new HashMap<>();
    private final HashMap<UUID, Integer> teleportRequestRequestees = new HashMap<>();
    private final HashMap<UUID, Integer> teleportingRequesters = new HashMap<>();
    private final HashMap<UUID, Integer> standingStillRequestees = new HashMap<>();

    /**
     * Initialize player teleport.
     *
     * @param globalTeleportingPlayers List of globally teleporting players.
     */
    public PlayerTeleport(List<UUID> globalTeleportingPlayers) {
        this.globalTeleportingPlayers = globalTeleportingPlayers;
    }

    /**
     * Get whether command is valid.
     *
     * @param args arguments
     * @return whether command is valid
     */

    private boolean isCommandValid(String[] args) {
        if (args.length == 0) {
            return false;
        }

        String teleportType = args[0];

        if (teleportType.equals(PlayerTeleportType.REQUEST.getType())) {
            return args.length == 2;
        }

        if (
                teleportType.equals(PlayerTeleportType.ACCEPT.getType())
                        || teleportType.equals(PlayerTeleportType.DECLINE.getType())
                        || teleportType.equals(PlayerTeleportType.DISCARD.getType())
        ) {
            return args.length == 1;
        }

        return false;
    }

    /**
     * Get player UUID from a map of teleporting players.
     *
     * @param teleportTaskId runnable ID for teleportation process
     * @param teleportTasks  teleporting players
     * @return player UUID
     */
    private @Nullable UUID getPlayerUuidFromTaskId(Integer teleportTaskId, HashMap<UUID, Integer> teleportTasks) {
        UUID playerUuid = null;

        for (Map.Entry<UUID, Integer> entry : teleportTasks.entrySet()) {
            if (entry.getValue().equals(teleportTaskId)) {
                playerUuid = entry.getKey();
            }
        }

        return playerUuid;
    }

    /**
     * Remove players from teleport queue and cancel the runnable.
     *
     * @param requesteeUuid  requestee UUID
     * @param requesterUuid  requester UUID
     * @param requesteeQueue requestee queue
     * @param requesterQueue requester queue
     * @param taskId         task ID
     */
    private void removePlayersFromQueueAndCancelRunnable(
            UUID requesteeUuid, UUID requesterUuid,
            HashMap<UUID, Integer> requesteeQueue, HashMap<UUID, Integer> requesterQueue,
            Integer taskId
    ) {
        requesteeQueue.remove(requesteeUuid);
        requesterQueue.remove(requesterUuid);

        Bukkit.getScheduler().cancelTask(taskId);
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args
    ) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (!this.isCommandValid(args)) {
            player.sendMessage(Component
                    .text(PlayerTeleportMessage.ERROR_USAGE.getMessage())
                    .color(NamedTextColor.GRAY)
            );

            return true;
        }

        String teleportType = args[0];

        if (Objects.equals(teleportType, PlayerTeleportType.REQUEST.getType())) {
            String teleportTarget = args[1];
            Player otherPlayer = Minedo.getInstance().getServer().getPlayer(teleportTarget);
            Integer existingTeleportRequestTaskId = teleportRequestRequesters.get(player.getUniqueId());

            if (globalTeleportingPlayers.contains(player.getUniqueId())) {
                player.sendMessage(Component
                        .text(GlobalTeleportMessage.ERROR_USE_MORE_THAN_ONCE_AT_A_TIME.getMessage())
                        .color(NamedTextColor.RED)
                );

                return true;
            }

            if (existingTeleportRequestTaskId != null) {
                player.sendMessage(Component
                        .text(PlayerTeleportMessage.ERROR_REQUEST_SENT_OUT.getMessage())
                        .color(NamedTextColor.RED)
                );
                return true;
            }

            if (otherPlayer != null && otherPlayer.isOnline()) {
                if (PlayerBlockedRepository.isPlayerBlockedByPlayer(
                        player.getUniqueId(), otherPlayer.getUniqueId())) {
                    player.sendMessage(Component
                            .text(String.format(
                                    IgnoreMessage.ERROR_INTERACT.getMessage(),
                                    otherPlayer.getName()
                            ))
                            .color(NamedTextColor.RED)
                    );

                    return true;
                }

                if (player.equals(otherPlayer)) {
                    player.sendMessage(Component
                            .text(PlayerTeleportMessage.ERROR_REQUEST_TO_SELF.getMessage())
                            .color(NamedTextColor.RED)
                    );

                    return true;
                }

                long DELAY = 30;
                int teleportRequestTaskId = new PlayerTeleportRequestScheduler(
                        player, otherPlayer, teleportRequestRequesters, teleportRequestRequestees
                )
                        .runTaskLater(Minedo.getInstance(), DELAY * (int) Common.TICK_PER_SECOND.getValue())
                        .getTaskId();

                this.teleportRequestRequesters.put(player.getUniqueId(), teleportRequestTaskId);
                this.teleportRequestRequestees.put(otherPlayer.getUniqueId(), teleportRequestTaskId);

                player.sendMessage(Component
                        .text(String.format(
                                PlayerTeleportMessage.SUCCESS_REQUEST_REQUESTEE.getMessage(),
                                otherPlayer.getName()
                        ))
                        .color(NamedTextColor.YELLOW)
                );

                otherPlayer.playSound(otherPlayer.getLocation(), FeedbackSound.INFO.getSound(), 1, 1);
                otherPlayer.sendMessage(Component
                        .text(String.format(
                                PlayerTeleportMessage.SUCCESS_REQUEST_REQUESTER.getMessage(),
                                player.getName()
                        ))
                        .color(NamedTextColor.YELLOW)
                );
            } else {
                player.sendMessage(Component
                        .text(PlayerTeleportMessage.ERROR_REQUEST_PLAYER_NOT_FOUND.getMessage())
                        .color(NamedTextColor.RED)
                );
            }

            return true;
        } else if (Objects.equals(teleportType, PlayerTeleportType.ACCEPT.getType())) {
            Minedo instance = Minedo.getInstance();
            Integer existingTeleportRequestTaskId = teleportRequestRequestees.get(player.getUniqueId());
            Integer existingTeleportingTaskId = teleportingRequesters.get(player.getUniqueId());
            Integer existingStandingStillTaskId = standingStillRequestees.get(player.getUniqueId());

            if (existingTeleportRequestTaskId == null) {
                player.sendMessage(Component
                        .text(PlayerTeleportMessage.ERROR_NO_REQUEST_RECEIVED.getMessage())
                        .color(NamedTextColor.RED)
                );

                return true;
            }

            if (existingTeleportingTaskId != null || existingStandingStillTaskId != null) {
                player.sendMessage(Component
                        .text(GlobalTeleportMessage.ERROR_USE_MORE_THAN_ONCE_AT_A_TIME.getMessage())
                        .color(NamedTextColor.RED)
                );

                return true;
            }

            UUID otherPlayerUuid = this.getPlayerUuidFromTaskId(
                    existingTeleportRequestTaskId,
                    teleportRequestRequesters
            );

            if (otherPlayerUuid == null) {
                player.sendMessage(Component
                        .text(PlayerTeleportMessage.ERROR_TELEPORTATION_PROCESS_NOT_FOUND.getMessage())
                        .color(NamedTextColor.RED)
                );

                return true;
            }

            Player otherPlayer = instance.getServer().getPlayer(otherPlayerUuid);

            this.removePlayersFromQueueAndCancelRunnable(
                    player.getUniqueId(), otherPlayerUuid,
                    this.teleportRequestRequestees,
                    this.teleportRequestRequesters,
                    existingTeleportRequestTaskId
            );

            if (otherPlayer != null && otherPlayer.isOnline()) {
                long DURATION = 1;
                long DELAY = 1;
                int teleportingTaskId = new PlayerTeleportScheduler(
                        otherPlayer, player, teleportingRequesters,
                        standingStillRequestees, globalTeleportingPlayers
                ).runTaskTimer(
                        instance,
                        DELAY * (int) Common.TICK_PER_SECOND.getValue(),
                        DURATION * (int) Common.TICK_PER_SECOND.getValue()
                ).getTaskId();

                this.globalTeleportingPlayers.add(player.getUniqueId());
                this.globalTeleportingPlayers.add(otherPlayerUuid);

                this.teleportingRequesters.put(otherPlayer.getUniqueId(), teleportingTaskId);
                this.standingStillRequestees.put(player.getUniqueId(), teleportingTaskId);

                player.sendMessage(Component
                        .text(String.format(
                                PlayerTeleportMessage.INFO_TELEPORTING_REQUESTEE.getMessage(),
                                otherPlayer.getName()
                        ))
                        .color(NamedTextColor.YELLOW)
                );

                otherPlayer.playSound(otherPlayer.getLocation(), FeedbackSound.INFO.getSound(), 1, 1);
                otherPlayer.sendMessage(Component
                        .text(String.format(
                                PlayerTeleportMessage.INFO_TELEPORTING_REQUESTER.getMessage(),
                                player.getName()
                        ))
                        .color(NamedTextColor.YELLOW)
                );
            }

            return true;
        } else if (Objects.equals(teleportType, PlayerTeleportType.DECLINE.getType())) {
            Integer existingTeleportRequestTaskId = teleportRequestRequestees.get(player.getUniqueId());

            if (existingTeleportRequestTaskId == null) {
                player.sendMessage(Component
                        .text(PlayerTeleportMessage.ERROR_NO_REQUEST_RECEIVED.getMessage())
                        .color(NamedTextColor.RED)
                );

                return true;
            }

            UUID otherPlayerUuid = this.getPlayerUuidFromTaskId(
                    existingTeleportRequestTaskId, teleportRequestRequesters
            );

            if (otherPlayerUuid == null) {
                player.sendMessage(Component
                        .text(PlayerTeleportMessage.ERROR_TELEPORTATION_PROCESS_NOT_FOUND.getMessage())
                        .color(NamedTextColor.RED)
                );

                return true;
            }

            Player otherPlayer = Minedo.getInstance().getServer().getPlayer(otherPlayerUuid);

            this.removePlayersFromQueueAndCancelRunnable(
                    player.getUniqueId(), otherPlayerUuid,
                    this.teleportRequestRequestees,
                    this.teleportRequestRequesters,
                    existingTeleportRequestTaskId
            );

            if (otherPlayer != null && otherPlayer.isOnline()) {
                otherPlayer.playSound(otherPlayer.getLocation(), FeedbackSound.ERROR.getSound(), 1, 1);
                otherPlayer.sendMessage(Component
                        .text(String.format(
                                PlayerTeleportMessage.ERROR_DECLINED_REQUESTER.getMessage(),
                                player.getName()
                        ))
                        .color(NamedTextColor.RED)
                );

                player.sendMessage(Component
                        .text(String.format(
                                PlayerTeleportMessage.ERROR_DECLINED_REQUESTEE.getMessage(),
                                otherPlayer.getName()
                        ))
                        .color(NamedTextColor.RED)
                );
            } else {
                player.sendMessage(Component
                        .text(PlayerTeleportMessage.ERROR_OFFLINE_DECLINED_REQUESTEE.getMessage())
                        .color(NamedTextColor.RED)
                );
            }

            return true;
        } else if (Objects.equals(teleportType, PlayerTeleportType.DISCARD.getType())) {
            Integer existingTeleportRequestTaskId = teleportRequestRequesters.get(player.getUniqueId());

            if (existingTeleportRequestTaskId == null) {
                player.sendMessage(Component
                        .text(PlayerTeleportMessage.ERROR_NO_REQUEST_SENT.getMessage())
                        .color(NamedTextColor.RED)
                );

                return true;
            }

            UUID otherPlayerUuid = this.getPlayerUuidFromTaskId(
                    existingTeleportRequestTaskId, teleportRequestRequestees
            );

            if (otherPlayerUuid == null) {
                player.sendMessage(Component
                        .text(PlayerTeleportMessage.ERROR_TELEPORTATION_PROCESS_NOT_FOUND.getMessage())
                        .color(NamedTextColor.RED)
                );

                return true;
            }

            Player otherPlayer = Minedo.getInstance().getServer().getPlayer(otherPlayerUuid);

            this.removePlayersFromQueueAndCancelRunnable(
                    otherPlayerUuid, player.getUniqueId(),
                    this.teleportRequestRequestees,
                    this.teleportRequestRequesters,
                    existingTeleportRequestTaskId
            );

            if (otherPlayer != null && otherPlayer.isOnline()) {
                otherPlayer.playSound(otherPlayer.getLocation(), FeedbackSound.ERROR.getSound(), 1, 1);
                otherPlayer.sendMessage(Component
                        .text(String.format(
                                PlayerTeleportMessage.INFO_DISCARD_REQUESTEE.getMessage(),
                                player.getName()
                        ))
                        .color(NamedTextColor.RED)
                );

                player.sendMessage(Component
                        .text(PlayerTeleportMessage.INFO_DISCARD_REQUESTER.getMessage())
                        .color(NamedTextColor.RED)
                );
            } else {
                player.sendMessage(Component
                        .text(PlayerTeleportMessage.INFO_DISCARD_REQUESTER.getMessage())
                        .color(NamedTextColor.RED)
                );
            }

            return true;
        } else {
            player.sendMessage(Component
                    .text(PlayerTeleportMessage.ERROR_USAGE.getMessage())
                    .color(NamedTextColor.GRAY)
            );

            return true;
        }
    }

    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args
    ) {
        List<String> completions = new ArrayList<>();

        if (!(sender instanceof Player player)) {
            return completions;
        }

        List<String> teleportTypes = new ArrayList<>() {{
            add(PlayerTeleportType.REQUEST.getType());
            add(PlayerTeleportType.ACCEPT.getType());
            add(PlayerTeleportType.DECLINE.getType());
            add(PlayerTeleportType.DISCARD.getType());
        }};

        if (args.length == 1) {
            completions.addAll(teleportTypes);
        } else if (args.length == 2 && Objects.equals(args[0], PlayerTeleportType.REQUEST.getType())) {
            Collection<? extends Player> onlinePlayers = Minedo.getInstance().getServer().getOnlinePlayers();

            completions.addAll(onlinePlayers.stream()
                    .filter(onlinePlayer -> onlinePlayer != player)
                    .map(Player::getName).toList()
            );
        }

        return completions;
    }

    /**
     * Cancel teleport.
     *
     * @param player            player
     * @param teleportTaskId    teleport task ID
     * @param teleportingPlayer teleporting players
     * @param isRequester       whether a requester or not
     */
    private void handleTeleportCancellation(
            Player player, Integer teleportTaskId, HashMap<UUID, Integer> teleportingPlayer, Boolean isRequester
    ) {
        Player otherPlayer = null;
        UUID playerUuid = player.getUniqueId();
        UUID otherPlayerUuid = null;

        // Get other player's UUID.
        for (Map.Entry<UUID, Integer> entry : teleportingPlayer.entrySet()) {
            if (Objects.equals(entry.getValue(), teleportTaskId)) {
                otherPlayerUuid = entry.getKey();
                otherPlayer = Minedo.getInstance().getServer().getPlayer(otherPlayerUuid);
            }
        }

        // Remove from global teleport list.
        this.globalTeleportingPlayers.remove(playerUuid);
        this.globalTeleportingPlayers.remove(otherPlayerUuid);

        // Remove from Map.
        if (isRequester) {
            this.teleportingRequesters.remove(playerUuid);
            this.standingStillRequestees.remove(otherPlayerUuid);
        } else {
            this.teleportingRequesters.remove(otherPlayerUuid);
            this.standingStillRequestees.remove(playerUuid);
        }

        // Cancel Bukkit Runnable.
        Bukkit.getScheduler().cancelTask(teleportTaskId);

        this.sendTeleportationCancelled(player);
        this.sendTeleportationCancelled(otherPlayer);
    }

    /**
     * Send player message on teleportation cancel.
     *
     * @param player player
     */
    private void sendTeleportationCancelled(Player player) {
        if (player != null && player.isOnline()) {
            player.playSound(player.getLocation(), FeedbackSound.ERROR.getSound(), 1, 1);
            player.sendMessage(Component
                    .text(PlayerTeleportMessage.ERROR_TELEPORTATION_CANCELLED.getMessage())
                    .color(NamedTextColor.RED)
            );
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (
                event.getFrom().getBlockX() != event.getTo().getBlockX() ||
                        event.getFrom().getBlockY() != event.getTo().getBlockY() ||
                        event.getFrom().getBlockZ() != event.getTo().getBlockZ()
        ) {
            Player player = event.getPlayer();
            UUID playerUuid = player.getUniqueId();
            Integer requesterTeleportTaskId = this.teleportingRequesters.get(playerUuid);
            Integer requesteeTeleportTaskId = this.standingStillRequestees.get(playerUuid);

            if (requesterTeleportTaskId != null) {
                this.handleTeleportCancellation(
                        player, requesterTeleportTaskId, standingStillRequestees, true
                );
            } else if (requesteeTeleportTaskId != null) {
                this.handleTeleportCancellation(
                        player, requesteeTeleportTaskId, teleportingRequesters, false
                );
            }
        }
    }

}
