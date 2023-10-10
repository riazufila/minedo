package net.minedo.mc.functionalities.customcommand.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minedo.mc.Minedo;
import net.minedo.mc.constants.command.feedbacksound.FeedbackSound;
import net.minedo.mc.constants.command.message.ignoremessage.IgnoreMessage;
import net.minedo.mc.constants.command.message.whispermessage.WhisperMessage;
import net.minedo.mc.repositories.playerblockedlistrepository.PlayerBlockedRepository;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Message command.
 */
public class Message implements CommandExecutor, TabCompleter {

    /**
     * Get whether command is valid.
     *
     * @param args arguments
     * @return whether command is valid
     */
    private boolean isCommandValid(String[] args) {
        return args.length >= 2;
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
                    .text(WhisperMessage.ERROR_USAGE.getMessage())
                    .color(NamedTextColor.GRAY)
            );

            return true;
        }

        String messageTarget = args[0];
        Player otherPlayer = Minedo.getInstance().getServer().getPlayer(messageTarget);

        if (otherPlayer != null && otherPlayer.isOnline()) {
            if (player.equals(otherPlayer)) {
                player.sendMessage(Component
                        .text(WhisperMessage.ERROR_MESSAGE_TO_SELF.getMessage())
                        .color(NamedTextColor.RED)
                );

                return true;
            }

            if (PlayerBlockedRepository.isPlayerBlockedByPlayer(
                    player.getUniqueId(), otherPlayer.getUniqueId())) {
                player.playSound(player.getLocation(), FeedbackSound.ERROR.getSound(), 1, 1);
                player.sendMessage(Component
                        .text(String.format(
                                IgnoreMessage.ERROR_INTERACT.getMessage(),
                                otherPlayer.getName()
                        ))
                        .color(NamedTextColor.RED)
                );

                return true;
            }

            String message = String.join(StringUtils.SPACE, Arrays.copyOfRange(args, 1, args.length));
            Component component = Component
                    .text(String.format("%s > %s: %s", player.getName(), otherPlayer.getName(), message))
                    .color(NamedTextColor.LIGHT_PURPLE)
                    .decoration(TextDecoration.ITALIC, true);

            player.sendMessage(component);
            otherPlayer.playSound(otherPlayer.getLocation(), FeedbackSound.INFO.getSound(), 1, 1);
            otherPlayer.sendMessage(component);
        } else {
            player.sendMessage(Component
                    .text(WhisperMessage.ERROR_PLAYER_NOT_FOUND.getMessage())
                    .color(NamedTextColor.RED)
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

        if (args.length == 1) {
            Collection<? extends Player> onlinePlayers = Minedo.getInstance().getServer().getOnlinePlayers();

            completions.addAll(onlinePlayers.stream()
                    .filter(onlinePlayer -> onlinePlayer != player)
                    .map(Player::getName).toList()
            );
        }

        return completions;
    }

}
