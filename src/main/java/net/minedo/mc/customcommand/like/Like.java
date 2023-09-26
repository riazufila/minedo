package net.minedo.mc.customcommand.like;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minedo.mc.Minedo;
import net.minedo.mc.constants.likemessage.LikeMessage;
import net.minedo.mc.models.playerlike.PlayerLike;
import net.minedo.mc.models.playerprofile.PlayerProfile;
import net.minedo.mc.repositories.playerlikerepository.PlayerLikeRepository;
import net.minedo.mc.repositories.playerprofilerepository.PlayerProfileRepository;
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

public class Like implements CommandExecutor, TabCompleter {

    private final Minedo pluginInstance;

    public Like(Minedo pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

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

        PlayerProfileRepository playerProfileRepository = new PlayerProfileRepository();
        PlayerProfile playerProfile = playerProfileRepository.getPlayerProfileByUuid(player.getUniqueId());
        PlayerLikeRepository playerLikeRepository = new PlayerLikeRepository();
        PlayerLike playerLike = playerLikeRepository.getPlayerLikeByPlayerId(playerProfile.getId());

        if (playerLike.isLikeSentRecently()) {
            player.sendMessage(Component
                    .text(LikeMessage.ERROR_LIKE_SENT_RECENTLY.getMessage())
                    .color(NamedTextColor.RED)
            );

            return true;
        }

        String likeTarget = args[0];
        Player otherPlayer = this.pluginInstance.getServer().getPlayer(likeTarget);

        if (otherPlayer != null && otherPlayer.isOnline()) {
            Instant instant = Instant.now();

            PlayerLike updatedPlayerLike = new PlayerLike();
            playerLike.setLikeSentCount(playerLike.getLikeSentCount() + 1);
            playerLike.setLastLikeSent(instant);
            playerLikeRepository.updatePlayerLike(playerLike.getPlayerId(), playerLike);

            PlayerProfileRepository otherPlayerProfileRepository = new PlayerProfileRepository();
            PlayerProfile otherPlayerProfile = otherPlayerProfileRepository
                    .getPlayerProfileByUuid(otherPlayer.getUniqueId());

            PlayerLikeRepository otherPlayerLikeRepository = new PlayerLikeRepository();
            PlayerLike otherPlayerLike = otherPlayerLikeRepository
                    .getPlayerLikeByPlayerId(otherPlayerProfile.getId());

            otherPlayerLike.setLikeReceivedCount(otherPlayerLike.getLikeReceivedCount() + 1);
            otherPlayerLikeRepository.updatePlayerLike(otherPlayerLike.getPlayerId(), otherPlayerLike);

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
                    .text(LikeMessage.ERROR_REQUEST_PLAYER_IS_NOT_IN_SERVER.getMessage())
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
            Collection<? extends Player> onlinePlayers = this.pluginInstance.getServer().getOnlinePlayers();

            completions.addAll(onlinePlayers.stream()
                    .filter(onlinePlayer -> onlinePlayer != player)
                    .map(Player::getName).toList()
            );
        }

        return completions;
    }

}
