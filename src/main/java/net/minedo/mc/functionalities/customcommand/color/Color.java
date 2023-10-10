package net.minedo.mc.functionalities.customcommand.color;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minedo.mc.constants.command.message.colormessage.ColorMessage;
import net.minedo.mc.constants.command.type.colortype.ColorType;
import net.minedo.mc.constants.groupcolor.GroupColor;
import net.minedo.mc.functionalities.permissions.PermissionUtils;
import net.minedo.mc.models.playercolor.PlayerColor;
import net.minedo.mc.repositories.playercolorrepository.PlayerColorRepository;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Color command.
 */
public class Color implements CommandExecutor, TabCompleter {

    /**
     * Get whether HEX is valid.
     *
     * @param color color in HEX
     * @return whether HEX is valid
     */
    private boolean isHexValid(@NotNull String color) {
        String HEX_REGEX = "^#([A-Fa-f0-9]{6})$";
        Pattern pattern = Pattern.compile(HEX_REGEX);
        Matcher matcher = pattern.matcher(color);

        return matcher.matches();
    }

    /**
     * Get whether command is valid.
     *
     * @param args arguments
     * @return whether command is valid
     */
    private boolean isCommandValid(@NotNull String[] args) {
        if (args.length != 3) {
            return false;
        }

        String chatOrName = args[0];
        String colorType = args[1];
        String color = args[2];
        boolean isValidColor = true;

        if (!color.equals(ColorType.REMOVE.getType())) {
            if (colorType.equals(ColorType.CUSTOM.getType())) {
                isValidColor = this.isHexValid(color);
            } else if (colorType.equals(ColorType.PRESET.getType())) {
                isValidColor = Arrays
                        .stream(GroupColor.values())
                        .map(Enum::name)
                        .anyMatch(name -> name.equalsIgnoreCase(color));
            }
        }

        return (chatOrName.equals(ColorType.NAME.getType()) || chatOrName.equals(ColorType.CHAT.getType()))
                && (colorType.equals(ColorType.PRESET.getType()) || colorType.equals(ColorType.CUSTOM.getType()))
                && (isValidColor);
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
                    .text(ColorMessage.ERROR_USAGE.getMessage())
                    .color(NamedTextColor.GRAY)
            );

            return true;
        }

        String chatOrName = args[0];
        String colorType = args[1];
        String color = args[2];

        if (!PermissionUtils.validatePlayerPermissionForColorSettingByColorTypeAndColor(player, colorType, color)) {
            player.sendMessage(Component
                    .text(ColorMessage.ERROR_NO_PERMISSION.getMessage())
                    .color(NamedTextColor.RED)
            );

            return true;
        }

        UUID playerUuid = player.getUniqueId();
        String updatedColor = color.equals(ColorType.REMOVE.getType()) ? null : color.toUpperCase();

        PlayerColor playerColor = PlayerColorRepository.getPlayerColorByPlayerUuid(playerUuid);
        String prefixPreset = playerColor.prefixPreset();
        String prefixCustom = playerColor.prefixCustom();
        String contentPreset = playerColor.contentPreset();
        String contentCustom = playerColor.contentCustom();

        if (chatOrName.equals(ColorType.NAME.getType())) {
            if (colorType.equals(ColorType.PRESET.getType())) {
                prefixPreset = updatedColor;
            } else if (colorType.equals(ColorType.CUSTOM.getType())) {
                prefixCustom = updatedColor;
            }
        } else if (chatOrName.equals(ColorType.CHAT.getType())) {
            if (colorType.equals(ColorType.PRESET.getType())) {
                contentPreset = updatedColor;
            } else if (colorType.equals(ColorType.CUSTOM.getType())) {
                contentCustom = updatedColor;
            }
        }

        PlayerColor updatedPlayerColor = new PlayerColor(prefixPreset, prefixCustom, contentPreset, contentCustom);
        PlayerColorRepository.updatePlayerColor(playerUuid, updatedPlayerColor);

        player.sendMessage(Component
                .text(ColorMessage.SUCCESS_COLOR_UPDATE.getMessage())
                .color(NamedTextColor.GREEN)
        );

        return true;
    }

    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args
    ) {
        List<String> completions = new ArrayList<>();

        if (!(sender instanceof Player player)) {
            return completions;
        }

        List<String> prefixOrContent = new ArrayList<>() {{
            add(ColorType.NAME.getType());
            add(ColorType.CHAT.getType());
        }};
        List<String> colorTypes = new ArrayList<>() {{
            add(ColorType.PRESET.getType());
            add(ColorType.CUSTOM.getType());
        }};
        List<String> presets = Arrays
                .stream(GroupColor.values())
                .map(groupColor -> groupColor.toString().toLowerCase())
                .toList();

        if (args.length == 1) {
            completions.addAll(prefixOrContent);
        } else if (args.length == 2 && prefixOrContent.contains(args[0])) {
            for (String colorType : colorTypes) {
                if (colorType.equals(ColorType.CUSTOM.getType())) {
                    if (PermissionUtils.validatePlayerPermissionForCustomColor(player)) {
                        completions.add(colorType);
                    }
                } else {
                    completions.add(colorType);
                }
            }
        } else if (args.length == 3 && colorTypes.contains(args[1])) {
            for (String preset : presets) {
                if (args[1].equals(ColorType.PRESET.getType())
                        && PermissionUtils.validatePlayerPermissionForPresetColor(player, preset)) {
                    completions.add(preset);
                }
            }

            completions.add(ColorType.REMOVE.getType());
        }

        return completions;
    }

}
