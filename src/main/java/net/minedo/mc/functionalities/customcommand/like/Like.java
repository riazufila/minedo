package net.minedo.mc.functionalities.customcommand.like;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minedo.mc.Minedo;
import net.minedo.mc.constants.command.message.likemessage.LikeMessage;
import net.minedo.mc.models.playerlike.PlayerLike;
import net.minedo.mc.repositories.playerlikerepository.PlayerLikeRepository;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Like command.
 */
public class Like implements CommandExecutor, TabCompleter {

    /**
     * Get whether command is valid.
     *
     * @param args arguments
     * @return whether command is valid
     */
    private boolean isCommandValid(String[] args) {
        return args.length == 1;
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
                    .text(LikeMessage.ERROR_USAGE.getMessage())
                    .color(NamedTextColor.GRAY)
            );

            return true;
        }

        PlayerLike playerLike = PlayerLikeRepository.getPlayerLikeByPlayerUuid(player.getUniqueId());

        if (playerLike.isLikeSentRecently()) {
            player.sendMessage(Component
                    .text(LikeMessage.ERROR_LIKE_SENT_RECENTLY.getMessage())
                    .color(NamedTextColor.RED)
            );

            return true;
        }

        String likeTarget = args[0];
        Player otherPlayer = Minedo.getInstance().getServer().getPlayer(likeTarget);

        if (player.equals(otherPlayer)) {
            player.sendMessage(Component
                    .text(LikeMessage.ERROR_INVALID_TARGET.getMessage())
                    .color(NamedTextColor.RED)
            );

            return true;
        }

        if (otherPlayer != null && otherPlayer.isOnline()) {
            Instant instant = Instant.now();

            PlayerLike updatedPlayerLike = new PlayerLike(
                    playerLike.likeReceivedCount(),
                    playerLike.likeSentCount() + 1,
                    instant
            );
            PlayerLikeRepository.updatePlayerLike(player.getUniqueId(), updatedPlayerLike);

            PlayerLike otherPlayerLike = PlayerLikeRepository
                    .getPlayerLikeByPlayerUuid(otherPlayer.getUniqueId());

            PlayerLike updatedOtherPlayerLike = new PlayerLike(
                    otherPlayerLike.likeReceivedCount() + 1,
                    otherPlayerLike.likeSentCount(),
                    instant
            );
            PlayerLikeRepository.updatePlayerLike(otherPlayer.getUniqueId(), updatedOtherPlayerLike);

            player.sendMessage(Component
                    .text(String.format(
                            LikeMessage.SUCCESS_LIKE_SENT.getMessage(),
                            otherPlayer.getName()
                    ))
                    .color(NamedTextColor.GREEN)
            );
            otherPlayer.sendMessage(Component
                    .text(String.format(
                            LikeMessage.SUCCESS_LIKE_RECEIVED.getMessage(),
                            player.getName()
                    ))
                    .color(NamedTextColor.GREEN)
            );
        } else {
            player.sendMessage(Component
                    .text(LikeMessage.ERROR_UNABLE_TO_FIND_PLAYER.getMessage())
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
