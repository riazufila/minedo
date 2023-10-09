package net.minedo.mc.functionalities.permissions;

import net.kyori.adventure.text.format.NamedTextColor;
import net.minedo.mc.constants.command.type.colortype.ColorType;
import net.minedo.mc.constants.groupcolor.GroupColor;
import net.minedo.mc.constants.grouppermission.GroupPermission;
import net.minedo.mc.functionalities.chat.ChatUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Permission helper.
 */
public final class PermissionUtils {

    /**
     * Get whether player permission is valid for custom color.
     *
     * @param player player
     * @return whether player permission is valid for custom color.
     */
    public static boolean validatePlayerPermissionForCustomColor(Player player) {
        return player.hasPermission(GroupPermission.OBSIDIAN.getPermission());
    }

    /**
     * Get whether player permission is valid for preset color.
     *
     * @param player player
     * @param color  color
     * @return whether player permission is valid for preset color
     */
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

    /**
     * Get color by permission.
     *
     * @param player player
     * @return color by permission
     */
    public static @Nullable NamedTextColor getColorByPermission(Player player) {
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

    /**
     * Get whether player permission is valid for color setting.
     *
     * @param player    player
     * @param colorType color type
     * @param color     color
     * @return whether player permission is valid for color setting
     */
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

    /**
     * Get whether player permission is valid for nickname display.
     *
     * @param player player
     * @return whether player permission is valid for nickname display
     */
    public static boolean validatePlayerPermissionForNicknameDisplay(Player player) {
        return player.hasPermission(GroupPermission.REDSTONE.getPermission());
    }

    /**
     * Get whether player permission is valid for nickname reveal.
     *
     * @param player player
     * @return whether player permission is valid for nickname reveal
     */
    public static boolean validatePlayerPermissionForNicknameReveal(Player player) {
        return player.hasPermission(GroupPermission.OBSIDIAN.getPermission());
    }

    /**
     * Get whether player permission is valid for home count.
     *
     * @param player    player
     * @param homeCount home count
     * @return whether player permission is valid for home count
     */
    public static boolean validatePlayerPermissionForHomeCount(Player player, int homeCount) {
        boolean isAllowed = false;
        int EMERALD_HOME_COUNT = 30;
        int GOLD_HOME_COUNT = 15;
        int DEFAULT_HOME_COUNT = 3;

        if (player.hasPermission(GroupPermission.DIAMOND.getPermission())) {
            isAllowed = true;
        } else if (player.hasPermission(GroupPermission.EMERALD.getPermission())
                && homeCount < EMERALD_HOME_COUNT
        ) {
            isAllowed = true;
        } else if (player.hasPermission(GroupPermission.GOLD.getPermission())
                && homeCount < GOLD_HOME_COUNT
        ) {
            isAllowed = true;
        } else if (homeCount < DEFAULT_HOME_COUNT) {
            isAllowed = true;
        }

        return isAllowed;
    }

}
