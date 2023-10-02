package net.minedo.mc.functionalities.customcommand.nickname;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minedo.mc.Minedo;
import net.minedo.mc.constants.command.message.nicknamemessage.NicknameMessage;
import net.minedo.mc.constants.command.type.nicknametype.NicknameType;
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

        // TODO: Check if nickname is already inside a list of mixed real names and nicknames.

        // TODO: Update, reveal, remove nickname.

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
            completions.add(NicknameType.REVEAL.getType());
            completions.add(NicknameType.REMOVE.getType());
        } else if (args.length == 2 && args[0].equals(NicknameType.REVEAL.getType())) {
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
