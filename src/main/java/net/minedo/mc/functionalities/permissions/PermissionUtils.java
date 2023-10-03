package net.minedo.mc.functionalities.permissions;

import net.kyori.adventure.text.format.NamedTextColor;
import net.minedo.mc.constants.command.type.colortype.ColorType;
import net.minedo.mc.constants.groupcolor.GroupColor;
import net.minedo.mc.constants.grouppermission.GroupPermission;
import net.minedo.mc.functionalities.chat.ChatUtils;
import org.bukkit.entity.Player;

public class PermissionUtils {

    public static boolean validatePlayerPermissionForCustomColor(Player player) {
        return player.hasPermission(GroupPermission.OBSIDIAN.getPermission());
    }

    public static boolean validatePlayerPermissionForPresetColor(Player player, String color) {
        if (ChatUtils.isGroupColorTheSame(color, GroupColor.OBSIDIAN)) {
            return player.hasPermission(GroupPermission.OBSIDIAN.getPermission());
        } else if (ChatUtils.isGroupColorTheSame(color, GroupColor.REDSTONE)) {
            return player.hasPermission(GroupPermission.REDSTONE.getPermission());
        } else if (ChatUtils.isGroupColorTheSame(color, GroupColor.DIAMOND)) {
            return player.hasPermission(GroupPermission.DIAMOND.getPermission());
        } else if (ChatUtils.isGroupColorTheSame(color, GroupColor.EMERALD)) {
            return player.hasPermission(GroupPermission.EMERALD.getPermission());
        } else if (ChatUtils.isGroupColorTheSame(color, GroupColor.GOLD)) {
            return player.hasPermission(GroupPermission.GOLD.getPermission());
        }

        return false;
    }

    public static NamedTextColor getColorByPermission(Player player) {
        NamedTextColor color = null;

        if (player.hasPermission(GroupPermission.OBSIDIAN.getPermission())) {
            color = GroupColor.OBSIDIAN.getColor();
        } else if (player.hasPermission(GroupPermission.REDSTONE.getPermission())) {
            color = GroupColor.REDSTONE.getColor();
        } else if (player.hasPermission(GroupPermission.DIAMOND.getPermission())) {
            color = GroupColor.DIAMOND.getColor();
        } else if (player.hasPermission(GroupPermission.EMERALD.getPermission())) {
            color = GroupColor.EMERALD.getColor();
        } else if (player.hasPermission(GroupPermission.GOLD.getPermission())) {
            color = GroupColor.GOLD.getColor();
        }

        return color;
    }

    public static boolean validatePlayerPermissionForColorSettingByColorTypeAndColor(
            Player player, String colorType, String color
    ) {
        if (color.equals(ColorType.REMOVE.getType())) {
            if (player.hasPermission(GroupPermission.GOLD.getPermission())) {
                return true;
            }
        }

        if (colorType.equals(ColorType.CUSTOM.getType())) {
            return player.hasPermission(GroupPermission.OBSIDIAN.getPermission());
        } else if (colorType.equals(ColorType.PRESET.getType())) {
            return validatePlayerPermissionForPresetColor(player, color);
        }

        return true;
    }

    public static boolean validatePlayerPermissionForNicknameDisplay(Player player) {
        return player.hasPermission(GroupPermission.REDSTONE.getPermission());
    }

    public static boolean validatePlayerPermissionForNicknameReveal(Player player) {
        return player.hasPermission(GroupPermission.OBSIDIAN.getPermission());
    }


}
