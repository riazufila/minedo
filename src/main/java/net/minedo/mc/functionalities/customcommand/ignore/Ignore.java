package net.minedo.mc.functionalities.customcommand.ignore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minedo.mc.Minedo;
import net.minedo.mc.constants.command.message.ignoremessage.IgnoreMessage;
import net.minedo.mc.constants.command.type.ignoretype.IgnoreType;
import net.minedo.mc.repositories.playerblockedlistrepository.PlayerBlockedRepository;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class Ignore implements CommandExecutor, TabCompleter {

    private final Minedo pluginInstance;

    public Ignore(Minedo pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    private boolean isCommandValid(String[] args) {
        if (args.length != 2) {
            return false;
        }

        String ignoreType = args[0];

        return ignoreType.equals(IgnoreType.ADD.getType()) || ignoreType.equals(IgnoreType.REMOVE.getType());
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
                    .text(IgnoreMessage.ERROR_USAGE.getMessage())
                    .color(NamedTextColor.GRAY)
            );

            return true;
        }

        String ignoreType = args[0];
        String ignoreTarget = args[1];
        Player otherPlayer = this.pluginInstance.getServer().getPlayer(ignoreTarget);

        if (otherPlayer != null) {
            if (otherPlayer.equals(player)) {
                player.sendMessage(Component
                        .text(IgnoreMessage.ERROR_TARGET.getMessage())
                        .color(NamedTextColor.RED)
                );

                return true;
            }
        }

        if (Objects.equals(ignoreType, IgnoreType.ADD.getType())) {
            if (otherPlayer != null && otherPlayer.isOnline()) {
                PlayerBlockedRepository playerBlockedRepository = new PlayerBlockedRepository();

                if (playerBlockedRepository.isPlayerBlockedByPlayer(
                        otherPlayer.getUniqueId(), player.getUniqueId())) {
                    player.sendMessage(Component
                            .text(String.format(
                                    IgnoreMessage.ERROR_ALREADY_BLOCKED.getMessage(),
                                    otherPlayer.getName()
                            ))
                            .color(NamedTextColor.RED)
                    );

                    return true;
                }

                playerBlockedRepository.addBlockedPlayer(player.getUniqueId(), otherPlayer.getUniqueId());

                player.sendMessage(Component
                        .text(String.format(
                                IgnoreMessage.SUCCESS_ADD_BLOCKED_PLAYER.getMessage(),
                                otherPlayer.getName()
                        ))
                        .color(NamedTextColor.GREEN)
                );
            } else {
                player.sendMessage(Component
                        .text(IgnoreMessage.ERROR_PLAYER_IS_NOT_IN_SERVER.getMessage())
                        .color(NamedTextColor.RED)
                );
            }
        } else if (Objects.equals(ignoreType, IgnoreType.REMOVE.getType())) {
            if (otherPlayer != null && otherPlayer.isOnline()) {
                PlayerBlockedRepository playerBlockedRepository = new PlayerBlockedRepository();

                if (!(playerBlockedRepository.isPlayerBlockedByPlayer(
                        otherPlayer.getUniqueId(), player.getUniqueId()))) {
                    player.sendMessage(Component
                            .text(String.format(
                                    IgnoreMessage.ERROR_NOT_BLOCKED.getMessage(),
                                    otherPlayer.getName()
                            ))
                            .color(NamedTextColor.RED)
                    );

                    return true;
                }

                playerBlockedRepository.removeBlockedPlayer(player.getUniqueId(), otherPlayer.getUniqueId());

                player.sendMessage(Component
                        .text(String.format(
                                IgnoreMessage.SUCCESS_REMOVE_BLOCK_PLAYER.getMessage(),
                                otherPlayer.getName()
                        ))
                        .color(NamedTextColor.GREEN)
                );
            } else {
                player.sendMessage(Component
                        .text(IgnoreMessage.ERROR_PLAYER_IS_NOT_IN_SERVER.getMessage())
                        .color(NamedTextColor.RED)
                );
            }
        } else {
            player.sendMessage(Component
                    .text(IgnoreMessage.ERROR_USAGE.getMessage())
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

        List<String> ignoreTypes = new ArrayList<>() {{
            add(IgnoreType.ADD.getType());
            add(IgnoreType.REMOVE.getType());
        }};

        if (args.length == 1) {
            completions.addAll(ignoreTypes);
        } else if (args.length == 2 && ignoreTypes.contains(args[0])) {
            Collection<? extends Player> onlinePlayers = this.pluginInstance.getServer().getOnlinePlayers();

            completions.addAll(onlinePlayers.stream()
                    .filter(onlinePlayer -> onlinePlayer != player)
                    .map(Player::getName).toList()
            );
        }

        return completions;
    }

}
