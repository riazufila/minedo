package net.minedo.mc.functionalities.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minedo.mc.constants.command.type.colortype.ColorType;
import net.minedo.mc.constants.groupcolor.GroupColor;
import net.minedo.mc.functionalities.permissions.PermissionUtils;
import net.minedo.mc.models.playercolor.PlayerColor;
import net.minedo.mc.repositories.playercolorrepository.PlayerColorRepository;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Chat utils.
 */
public final class ChatUtils {

    /**
     * Update component color.
     *
     * @param player    player
     * @param component component
     * @param isContent whether is content
     * @return updated component color
     */
    public static @NotNull Component updateComponentColor(Player player, Component component, boolean isContent) {
        ChatInfo chatInfo = getChatInfo(player, isContent);
        String selectedColor = chatInfo.selectedColor();
        boolean isCustom = chatInfo.custom();

        if (selectedColor != null) {
            if (!PermissionUtils.validatePlayerPermissionForColorSettingByColorTypeAndColor(player, isCustom
                    ? ColorType.CUSTOM.getType() : ColorType.PRESET.getType(), selectedColor)) {
                return component;
            }

            if (isCustom) {
                component = component.color(TextColor.fromHexString(selectedColor));
            } else {
                component = component.color(GroupColor.valueOf(selectedColor).getColor());
            }
        } else {
            component = component.color(PermissionUtils.getColorByPermission(player));
        }

        return component;
    }

    /**
     * Select color by color type.
     *
     * @param playerColor player color
     * @param player      player
     * @param isContent   whether is content
     * @return selected color
     */
    private static @NotNull ChatInfo selectColor(PlayerColor playerColor, Player player, boolean isContent) {
        String selectedColor = isContent ? playerColor.contentCustom() : playerColor.prefixCustom();
        boolean isCustom = false;

        if (selectedColor != null
                && PermissionUtils.validatePlayerPermissionForColorSettingByColorTypeAndColor(
                player, ColorType.CUSTOM.getType(), selectedColor
        )) {
            isCustom = true;
        } else {
            selectedColor = isContent ? playerColor.contentPreset() : playerColor.prefixPreset();
        }

        return new ChatInfo(isCustom, selectedColor);
    }

    /**
     * Get chat info.
     *
     * @param player    player
     * @param isContent whether is content
     * @return chat info
     */
    private static @NotNull ChatInfo getChatInfo(Player player, boolean isContent) {
        UUID playerUuid = player.getUniqueId();
        PlayerColor playerColor = PlayerColorRepository.getPlayerColorByPlayerUuid(playerUuid);

        if (isContent) {
            return selectColor(playerColor, player, true);
        } else {
            return selectColor(playerColor, player, false);
        }
    }

    /**
     * Get whether group color is same.
     *
     * @param color      color
     * @param groupColor group color values as in {@link GroupColor#values()}
     * @return whether group color is same
     */
    public static boolean isGroupColorTheSame(String color, GroupColor groupColor) {
        return GroupColor.valueOf(color.toUpperCase()).equals(groupColor);
    }

}
