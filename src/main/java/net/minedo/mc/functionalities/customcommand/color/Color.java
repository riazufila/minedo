package net.minedo.mc.functionalities.customcommand.color;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minedo.mc.constants.command.message.colormessage.ColorMessage;
import net.minedo.mc.constants.command.type.colortype.ColorType;
import net.minedo.mc.constants.groupcolor.GroupColor;
import net.minedo.mc.functionalities.chat.ChatUtils;
import net.minedo.mc.models.playercolor.PlayerColor;
import net.minedo.mc.repositories.playercolorrepository.PlayerColorRepository;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Color implements CommandExecutor, TabCompleter {

    private boolean isCommandValid(String[] args) {
        if (args.length != 3) {
            return false;
        }

        String chatOrName = args[0];
        String colorType = args[1];

        return (chatOrName.equals(ColorType.NAME.getType()) || chatOrName.equals(ColorType.CHAT.getType()))
                && (colorType.equals(ColorType.PRESET.getType()) || colorType.equals(ColorType.CUSTOM.getType()));
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
                    .text(ColorMessage.ERROR_USAGE.getMessage())
                    .color(NamedTextColor.GRAY)
            );

            return true;
        }

        String chatOrName = args[0];
        String colorType = args[1];
        String color = args[2].toUpperCase();

        if (!ChatUtils.verifyPlayerPermissionForColorSetting(player, colorType, color)) {
            player.sendMessage(Component
                    .text(ColorMessage.ERROR_NO_PERMISSION.getMessage())
                    .color(NamedTextColor.RED)
            );

            return true;
        }

        UUID playerUuid = player.getUniqueId();
        PlayerColorRepository playerColorRepository = new PlayerColorRepository();
        PlayerColor playerColor = playerColorRepository.getPlayerColorByPlayerUuid(playerUuid);

        if (chatOrName.equals(ColorType.NAME.getType())) {
            if (colorType.equals(ColorType.PRESET.getType())) {
                playerColor.setPrefixPreset(color);
            } else if (colorType.equals(ColorType.CUSTOM.getType())) {
                playerColor.setPrefixCustom(color);
            }
        } else if (chatOrName.equals(ColorType.CHAT.getType())) {
            if (colorType.equals(ColorType.PRESET.getType())) {
                playerColor.setContentPreset(color);
            } else if (colorType.equals(ColorType.CUSTOM.getType())) {
                playerColor.setContentCustom(color);
            }
        }

        playerColorRepository.updatePlayerColor(playerUuid, playerColor);

        player.sendMessage(Component
                .text(ColorMessage.SUCCESS_COLOR_UPDATE.getMessage())
                .color(NamedTextColor.GREEN)
        );

        return true;
    }

    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args
    ) {
        List<String> completions = new ArrayList<>();

        if (!(sender instanceof Player)) {
            return completions;
        }

        if (args.length == 1) {
            completions.add(ColorType.NAME.getType());
            completions.add(ColorType.CHAT.getType());
        } else if (args.length == 2) {
            completions.add(ColorType.PRESET.getType());
            completions.add(ColorType.CUSTOM.getType());
        } else if (args[1].equals(ColorType.PRESET.getType()) && args.length == 3) {
            for (GroupColor color : GroupColor.values()) {
                completions.add(color.toString().toLowerCase());
            }
        }

        return completions;
    }

}
