package net.minedo.mc.functionalities.customcommand.narrate;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minedo.mc.Minedo;
import net.minedo.mc.constants.command.message.narratemessage.NarrateMessage;
import net.minedo.mc.functionalities.chat.ChatUtils;
import net.minedo.mc.repositories.playerblockedlistrepository.PlayerBlockedRepository;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Narrate implements CommandExecutor, TabCompleter {

    private final Minedo pluginInstance;

    public Narrate(Minedo pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    private boolean isCommandValid(String[] args) {
        return args.length != 0;
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
                    .text(NarrateMessage.ERROR_USAGE.getMessage())
                    .color(NamedTextColor.GRAY)
            );

            return true;
        }

        Collection<? extends Player> onlinePlayers = this.pluginInstance.getServer().getOnlinePlayers();

        for (Player onlinePlayer : onlinePlayers) {
            PlayerBlockedRepository playerBlockedRepository = new PlayerBlockedRepository();

            if (!(playerBlockedRepository.isPlayerBlockedByPlayer(
                    player.getUniqueId(), onlinePlayer.getUniqueId()))) {
                String content = String.join(StringUtils.SPACE, args);
                String STRING_FORMAT = "\"%s\" says %s.";
                Component component = Component.text(String.format(STRING_FORMAT, content, player.getName()));

                component = ChatUtils
                        .updateComponentColor(player, component, true)
                        .decoration(TextDecoration.ITALIC, true);

                onlinePlayer.sendMessage(component);
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args
    ) {
        return Collections.emptyList();
    }

}
