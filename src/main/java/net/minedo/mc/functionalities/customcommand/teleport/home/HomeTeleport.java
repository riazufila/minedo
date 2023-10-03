package net.minedo.mc.functionalities.customcommand.teleport.home;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minedo.mc.Minedo;
import net.minedo.mc.constants.command.message.globalteleportmessage.GlobalTeleportMessage;
import net.minedo.mc.constants.command.message.hometeleportmessage.HomeTeleportMessage;
import net.minedo.mc.constants.command.type.hometype.HomeType;
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

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeTeleport implements CommandExecutor, Listener, TabCompleter {

    private final List<UUID> globalTeleportingPlayers;
    private final Minedo pluginInstance;
    private final HashMap<UUID, Integer> teleportingPlayers = new HashMap<>();

    public HomeTeleport(List<UUID> globalTeleportingPlayers, Minedo pluginInstance) {
        this.globalTeleportingPlayers = globalTeleportingPlayers;
        this.pluginInstance = pluginInstance;
    }

    private boolean isValidHomeName(String nickname) {
        if (nickname == null) {
            return false;
        }

        String NAME_REGEX = "^[a-zA-Z][a-zA-Z0-9-]{0,19}$";
        Pattern pattern = Pattern.compile(NAME_REGEX);
        Matcher matcher = pattern.matcher(nickname);

        return matcher.matches();
    }

    private boolean isCommandValid(String[] args) {
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
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args
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
            PlayerHomeRepository playerHomeRepository = new PlayerHomeRepository();
            PlayerHome playerHome = playerHomeRepository.getPlayerHome(player.getUniqueId(), homeName);

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

            // TODO: Teleport to home.
        } else if (homeType.equals(HomeType.ADD.getType())) {
            PlayerHomeRepository playerHomeRepository = new PlayerHomeRepository();
            List<PlayerHome> homeList = playerHomeRepository.getPlayerHomeList(player.getUniqueId());
            int homeCount = homeList.size();
            boolean isAllowed = PermissionUtils.validatePlayerPermissionForHomeCount(player, homeCount);
            boolean isHomeNameUnique = homeList.stream().noneMatch(home -> Objects.equals(home.getName(), homeName));

            if (isAllowed && isHomeNameUnique) {
                playerHomeRepository.upsertHome(player.getUniqueId(), player.getLocation(), homeName);

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
            PlayerHomeRepository playerHomeRepository = new PlayerHomeRepository();
            playerHomeRepository.upsertHome(player.getUniqueId(), player.getLocation(), homeName);

            player.sendMessage(Component
                    .text(HomeTeleportMessage.SUCCESS_UPDATE_HOME.getMessage())
                    .color(NamedTextColor.GREEN)
            );
        } else if (homeType.equals(HomeType.REMOVE.getType())) {
            PlayerHomeRepository playerHomeRepository = new PlayerHomeRepository();
            playerHomeRepository.removeHome(player.getUniqueId(), homeName);

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
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args
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
            PlayerHomeRepository playerHomeRepository = new PlayerHomeRepository();
            List<String> homes = playerHomeRepository
                    .getPlayerHomeList(player.getUniqueId())
                    .stream()
                    .map(PlayerHome::getName)
                    .toList();

            completions.addAll(homes);
        }

        return completions;
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
            Integer teleportTaskId = this.teleportingPlayers.get(playerUuid);

            if (teleportTaskId != null) {
                // Remove from global teleporting list.
                this.globalTeleportingPlayers.remove(player.getUniqueId());
                // Remove from Map.
                this.teleportingPlayers.remove(playerUuid);
                // Cancel Bukkit Runnable.
                Bukkit.getScheduler().cancelTask(teleportTaskId);

                player.sendMessage(Component
                        .text(HomeTeleportMessage.ERROR_TELEPORTATION_CANCELLED.getMessage())
                        .color(NamedTextColor.RED)
                );
            }
        }
    }

}
