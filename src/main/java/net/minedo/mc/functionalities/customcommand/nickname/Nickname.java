package net.minedo.mc.functionalities.customcommand.nickname;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minedo.mc.Minedo;
import net.minedo.mc.constants.command.message.nicknamemessage.NicknameMessage;
import net.minedo.mc.constants.command.type.nicknametype.NicknameType;
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
import java.util.List;

public class Nickname implements CommandExecutor, TabCompleter, Listener {

    private final Minedo pluginInstance;

    public Nickname(Minedo pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    private boolean isCommandValid(String[] args) {
        // TODO: Validate command usage.

        // TODO: Validate nickname format.

        return true;
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
            List<String> otherPlayersNickname = playerProfileRepository.getOtherPlayersNickname(player.getUniqueId());

            completions.addAll(otherPlayersNickname);
        }

        return completions;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // TODO: Remove existing nickname for when a new player with the same real name joins.
    }

}
