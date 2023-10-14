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

/**
 * Narrate command.
 */
public class Narrate implements CommandExecutor, TabCompleter {

    /**
     * Get whether command is valid.
     *
     * @param args arguments
     * @return whether command is valid
     */
    private boolean isCommandValid(@NotNull String[] args) {
        return args.length != 0;
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
                    .text(NarrateMessage.ERROR_USAGE.getMessage())
                    .color(NamedTextColor.GRAY)
            );

            return true;
        }

        Collection<? extends Player> onlinePlayers = Minedo.getInstance().getServer().getOnlinePlayers();

        for (Player onlinePlayer : onlinePlayers) {
            if (!(PlayerBlockedRepository.isPlayerBlockedByPlayer(
                    player.getUniqueId(), onlinePlayer.getUniqueId()))) {
                Component playerNameComponent = Component.text(player.getName());
                Component contentComponent = Component.text(String.join(StringUtils.SPACE, args));

                Component combinedComponents = Component
                        .textOfChildren(
                                Component.text("\""),
                                ChatUtils.updateComponentColor(player, contentComponent, true),
                                Component.text("\" says "),
                                ChatUtils.updateComponentColor(player, playerNameComponent, false),
                                Component.text(".")
                        )
                        .decoration(TextDecoration.ITALIC, true);

                onlinePlayer.sendMessage(combinedComponents);
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args
    ) {
        return Collections.emptyList();
    }

}
