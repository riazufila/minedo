package net.minedo.mc.functionalities.customcommand.nickname;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minedo.mc.Minedo;
import net.minedo.mc.constants.command.message.nicknamemessage.NicknameMessage;
import net.minedo.mc.constants.command.type.nicknametype.NicknameType;
import net.minedo.mc.functionalities.chat.ChatUtils;
import net.minedo.mc.models.playerprofile.PlayerProfile;
import net.minedo.mc.repositories.playerprofilerepository.PlayerProfileRepository;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Nickname implements CommandExecutor, TabCompleter, Listener {

    private final Minedo pluginInstance;

    public Nickname(Minedo pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    private boolean isValidNickname(String nickname) {
        if (nickname == null) {
            return false;
        }

        String NAME_REGEX = "^[a-zA-Z][a-zA-Z0-9]{0,19}$";
        Pattern pattern = Pattern.compile(NAME_REGEX);
        Matcher matcher = pattern.matcher(nickname);

        return matcher.matches();
    }

    private boolean isCommandValid(String[] args) {
        if (args.length == 0) {
            return false;
        }

        String nicknameType = args[0];

        if (nicknameType.equals(NicknameType.SET.getType())
                || nicknameType.equals(NicknameType.REVEAL.getType())) {
            return args.length == 2;
        }

        if (nicknameType.equals(NicknameType.REMOVE.getType())) {
            return args.length == 1;
        }

        return false;
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
                    .text(NicknameMessage.ERROR_USAGE.getMessage())
                    .color(NamedTextColor.GRAY)
            );

            return true;
        }

        String nicknameType = args[0];

        if (nicknameType.equals(NicknameType.SET.getType())
                || nicknameType.equals(NicknameType.REVEAL.getType())) {
            String nickname = args[1];

            if (!this.isValidNickname(nickname)) {
                player.sendMessage(Component
                        .text(NicknameMessage.ERROR_INVALID_NICKNAME.getMessage())
                        .color(NamedTextColor.RED)
                );

                return true;
            }
        }

        if (nicknameType.equals(NicknameType.SET.getType())) {
            String nickname = args[1];

            PlayerProfileRepository playerProfileRepository = new PlayerProfileRepository();
            List<String> otherPlayersNickname = playerProfileRepository.getOtherPlayersNickname(player.getUniqueId());
            List<String> otherPlayersRealName = this.pluginInstance.getServer()
                    .getOnlinePlayers()
                    .stream()
                    .filter(otherPlayer -> otherPlayer != player)
                    .map(Player::getName)
                    .toList();

            List<String> realNamesAndNicknames = Stream
                    .concat(otherPlayersNickname.stream().map(String::toUpperCase),
                            otherPlayersRealName.stream().map(String::toUpperCase))
                    .toList();

            // Nickname is unique among a collection of online players' real name
            // and offline and online players' nickname.
            if (realNamesAndNicknames.contains(nickname.toUpperCase())) {
                player.sendMessage(Component
                        .text(NicknameMessage.INFO_NICKNAME_TAKEN.getMessage())
                        .color(NamedTextColor.YELLOW)
                );

                return true;
            }

            playerProfileRepository.updatePlayerNickname(player.getUniqueId(), nickname);

            player.sendMessage(Component
                    .text(NicknameMessage.SUCCESS_SET_NICKNAME.getMessage())
                    .color(NamedTextColor.GREEN)
            );
        } else if (nicknameType.equals(NicknameType.REVEAL.getType())) {
            if (!ChatUtils.validatePlayerPermissionForNicknameReveal(player)) {
                player.sendMessage(Component
                        .text(NicknameMessage.ERROR_NO_PERMISSION.getMessage())
                        .color(NamedTextColor.RED)
                );

                return true;
            }

            String nickname = args[1];
            PlayerProfileRepository playerProfileRepository = new PlayerProfileRepository();
            PlayerProfile playerProfile = playerProfileRepository.getPlayerProfileByNickname(nickname);

            // Verify player's real name and make sure player is online.
            if (playerProfile != null) {
                Player realPlayer = this.pluginInstance.getServer().getPlayer(playerProfile.getUuid());

                if (realPlayer != null) {
                    player.sendMessage(Component
                            .text(String.format(
                                    NicknameMessage.SUCCESS_REVEAL_NICKNAME.getMessage(),
                                    nickname,
                                    realPlayer.getName()))
                            .color(NamedTextColor.GREEN)
                    );

                    return true;
                }
            }

            // If somehow, the all the verifications above fails, then player will be notified of failure
            // to reveal nickname.
            player.sendMessage(Component
                    .text(NicknameMessage.ERROR_UNABLE_TO_PINPOINT.getMessage())
                    .color(NamedTextColor.RED)
            );
        } else if (nicknameType.equals(NicknameType.REMOVE.getType())) {
            PlayerProfileRepository playerProfileRepository = new PlayerProfileRepository();
            playerProfileRepository.updatePlayerNickname(player.getUniqueId(), null);

            player.sendMessage(Component
                    .text(NicknameMessage.SUCCESS_REMOVE_NICKNAME.getMessage())
                    .color(NamedTextColor.GREEN)
            );
        } else {
            player.sendMessage(Component
                    .text(NicknameMessage.ERROR_USAGE.getMessage())
                    .color(NamedTextColor.GRAY)
            );

            return true;
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
            completions.add(NicknameType.SET.getType());
            completions.add(NicknameType.REMOVE.getType());

            if (ChatUtils.validatePlayerPermissionForNicknameReveal(player)) {
                completions.add(NicknameType.REVEAL.getType());
            }
        } else if (args.length == 2
                && args[0].equals(NicknameType.REVEAL.getType())
                && ChatUtils.validatePlayerPermissionForNicknameReveal(player)) {
            PlayerProfileRepository playerProfileRepository = new PlayerProfileRepository();
            Collection<? extends Player> onlinePlayers = this.pluginInstance.getServer().getOnlinePlayers();

            if (!onlinePlayers.isEmpty()) {
                List<String> otherPlayersNickname = playerProfileRepository.getOtherPlayersNickname(
                        player.getUniqueId(),
                        onlinePlayers.stream().map(Player::getUniqueId).toList()
                );

                completions.addAll(otherPlayersNickname);
            }
        }

        return completions;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player newPlayer = event.getPlayer();
        PlayerProfileRepository playerProfileRepository = new PlayerProfileRepository();
        PlayerProfile existingPlayerProfile = playerProfileRepository.getPlayerProfileByNickname(newPlayer.getName());

        if (existingPlayerProfile != null) {
            playerProfileRepository.updatePlayerNickname(existingPlayerProfile.getUuid(), null);
        }
    }

}
