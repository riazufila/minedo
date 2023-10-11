package net.minedo.mc.functionalities.customcommand.teleport.home;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minedo.mc.Minedo;
import net.minedo.mc.constants.command.message.globalteleportmessage.GlobalTeleportMessage;
import net.minedo.mc.constants.command.message.hometeleportmessage.HomeTeleportMessage;
import net.minedo.mc.constants.command.type.hometype.HomeType;
import net.minedo.mc.constants.common.Common;
import net.minedo.mc.constants.feedbacksound.FeedbackSound;
import net.minedo.mc.functionalities.common.utils.PlayerUtils;
import net.minedo.mc.functionalities.permissions.PermissionUtils;
import net.minedo.mc.models.playerhome.PlayerHome;
import net.minedo.mc.repositories.playerhomerepository.PlayerHomeRepository;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Home teleport.
 */
public class HomeTeleport implements CommandExecutor, Listener, TabCompleter {

    private final List<UUID> globalTeleportingPlayers;
    private final HashMap<UUID, Integer> teleportingPlayers = new HashMap<>();

    /**
     * Initialize home teleport.
     *
     * @param globalTeleportingPlayers list of globally teleporting players
     */
    public HomeTeleport(@NotNull List<UUID> globalTeleportingPlayers) {
        this.globalTeleportingPlayers = globalTeleportingPlayers;
    }

    /**
     * Get whether home name is valid.
     *
     * @param homeName home name
     * @return whether home name is valid
     */
    private boolean isValidHomeName(@Nullable String homeName) {
        if (homeName == null) {
            return false;
        }

        String NAME_REGEX = "^[a-zA-Z][a-zA-Z0-9-]{0,19}$";
        Pattern pattern = Pattern.compile(NAME_REGEX);
        Matcher matcher = pattern.matcher(homeName);

        return matcher.matches();
    }

    /**
     * Get whether command is valid.
     *
     * @param args arguments
     * @return whether command is valid
     */
    private boolean isCommandValid(@NotNull String[] args) {
        if (args.length != 2) {
            return false;
        }

        String homeType = args[0];

        return homeType.equals(HomeType.TELEPORT.getType())
                || homeType.equals(HomeType.ADD.getType())
                || homeType.equals(HomeType.UPDATE.getType())
                || homeType.equals(HomeType.REMOVE.getType());
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
                    .text(HomeTeleportMessage.ERROR_USAGE.getMessage())
                    .color(NamedTextColor.GRAY)
            );

            return true;
        }

        String homeType = args[0];
        String homeName = args[1];

        if (homeType.equals(HomeType.TELEPORT.getType())
                || homeType.equals(HomeType.UPDATE.getType())
                || homeType.equals(HomeType.REMOVE.getType())
        ) {
            PlayerHome playerHome = PlayerHomeRepository.getPlayerHome(player.getUniqueId(), homeName);

            if (playerHome == null) {
                player.sendMessage(Component
                        .text(HomeTeleportMessage.ERROR_HOME_DOES_NOT_EXISTS.getMessage())
                        .color(NamedTextColor.RED)
                );

                return true;
            }
        }

        if (!this.isValidHomeName(homeName)) {
            player.sendMessage(Component
                    .text(HomeTeleportMessage.ERROR_INVALID_NAME.getMessage())
                    .color(NamedTextColor.RED)
            );

            return true;
        }

        if (homeType.equals(HomeType.TELEPORT.getType())) {
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

            PlayerHome playerHome = PlayerHomeRepository.getPlayerHome(player.getUniqueId(), homeName);

            if (playerHome == null) {
                player.sendMessage(Component
                        .text(HomeTeleportMessage.ERROR_HOME_DOES_NOT_EXISTS.getMessage())
                        .color(NamedTextColor.RED)
                );

                return true;
            }

            long DURATION = 1;
            long DELAY = 1;
            int teleportTaskId = new HomeTeleportScheduler(
                    player, playerHome,
                    this.globalTeleportingPlayers, this.teleportingPlayers
            ).runTaskTimer(
                    Minedo.getInstance(),
                    DELAY * (int) Common.TICK_PER_SECOND.getValue(),
                    DURATION * (int) Common.TICK_PER_SECOND.getValue()
            ).getTaskId();

            this.globalTeleportingPlayers.add(player.getUniqueId());
            this.teleportingPlayers.put(player.getUniqueId(), teleportTaskId);

            player.sendMessage(Component
                    .text(String.format(HomeTeleportMessage.INFO_TELEPORTING.getMessage(), homeName))
                    .color(NamedTextColor.YELLOW)
            );
        } else if (homeType.equals(HomeType.ADD.getType())) {
            List<PlayerHome> homeList = PlayerHomeRepository.getPlayerHomeList(player.getUniqueId());

            if (homeList == null) {
                homeList = new ArrayList<>();
            }

            int homeCount = homeList.size();
            boolean isAllowed = PermissionUtils.validatePlayerPermissionForHomeCount(player, homeCount);
            boolean isHomeNameUnique = homeList.stream().noneMatch(home -> Objects.equals(home.name(), homeName));

            if (isAllowed && isHomeNameUnique) {
                PlayerHomeRepository.upsertHome(player.getUniqueId(), player.getLocation(), homeName);

                player.sendMessage(Component
                        .text(HomeTeleportMessage.SUCCESS_ADD_HOME.getMessage())
                        .color(NamedTextColor.GREEN)
                );
            } else if (!isAllowed) {
                player.sendMessage(Component
                        .text(HomeTeleportMessage.ERROR_MAX_HOME.getMessage())
                        .color(NamedTextColor.RED)
                );
            } else {
                player.sendMessage(Component
                        .text(HomeTeleportMessage.ERROR_HOME_NAME_NOT_UNIQUE.getMessage())
                        .color(NamedTextColor.RED)
                );
            }
        } else if (homeType.equals(HomeType.UPDATE.getType())) {
            PlayerHomeRepository.upsertHome(player.getUniqueId(), player.getLocation(), homeName);

            player.sendMessage(Component
                    .text(HomeTeleportMessage.SUCCESS_UPDATE_HOME.getMessage())
                    .color(NamedTextColor.GREEN)
            );
        } else if (homeType.equals(HomeType.REMOVE.getType())) {
            PlayerHomeRepository.removeHome(player.getUniqueId(), homeName);

            player.sendMessage(Component
                    .text(HomeTeleportMessage.SUCCESS_REMOVE_HOME.getMessage())
                    .color(NamedTextColor.GREEN)
            );
        } else {
            player.sendMessage(Component
                    .text(HomeTeleportMessage.ERROR_USAGE.getMessage())
                    .color(NamedTextColor.GRAY)
            );
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args
    ) {
        List<String> completions = new ArrayList<>();

        if (!(sender instanceof Player player)) {
            return completions;
        }

        List<String> homeTypes = new ArrayList<>() {{
            add(HomeType.TELEPORT.getType());
            add(HomeType.ADD.getType());
            add(HomeType.UPDATE.getType());
            add(HomeType.REMOVE.getType());
        }};

        String homeType = args[0];

        if (args.length == 1) {
            completions.addAll(homeTypes);
        } else if (args.length == 2
                && (homeType.equals(HomeType.TELEPORT.getType())
                || homeType.equals(HomeType.REMOVE.getType())
                || homeType.equals(HomeType.UPDATE.getType()))
        ) {
            List<PlayerHome> playerHomeList = PlayerHomeRepository.getPlayerHomeList(player.getUniqueId());

            if (playerHomeList != null) {
                List<String> homes = playerHomeList
                        .stream()
                        .map(PlayerHome::name)
                        .toList();

                completions.addAll(homes);
            }
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

            player.playSound(player.getLocation(), FeedbackSound.ERROR.getSound(), 1, 1);
            player.sendMessage(Component
                    .text(HomeTeleportMessage.ERROR_TELEPORTATION_CANCELLED.getMessage())
                    .color(NamedTextColor.RED)
            );
        }
    }

}
