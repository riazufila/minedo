package net.minedo.mc.functionalities.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minedo.mc.constants.command.type.colortype.ColorType;
import net.minedo.mc.constants.groupcolor.GroupColor;
import net.minedo.mc.functionalities.permissions.PermissionUtils;
import net.minedo.mc.models.playercolor.PlayerColor;
import net.minedo.mc.repositories.playercolorrepository.PlayerColorRepository;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class ChatUtils {

    public static Component updateComponentColor(Player player, Component component, boolean isContent) {
        ChatInfo chatInfo = getChatInfo(player, isContent);
        String selectedColor = chatInfo.getSelectedColor();
        boolean isCustom = chatInfo.isCustom();

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

    private static ChatInfo selectColor(PlayerColor playerColor, Player player, boolean isContent) {
        String selectedColor = isContent ? playerColor.getContentCustom() : playerColor.getPrefixCustom();
        boolean isCustom = false;

        if (selectedColor != null
                && PermissionUtils.validatePlayerPermissionForColorSettingByColorTypeAndColor(
                player, ColorType.CUSTOM.getType(), selectedColor
        )) {
            isCustom = true;
        } else {
            selectedColor = isContent ? playerColor.getContentPreset() : playerColor.getPrefixPreset();
        }

        return new ChatInfo(isCustom, selectedColor);
    }

    private static ChatInfo getChatInfo(Player player, boolean isContent) {
        UUID playerUuid = player.getUniqueId();
        PlayerColor playerColor = PlayerColorRepository.getPlayerColorByPlayerUuid(playerUuid);

        if (isContent) {
            return selectColor(playerColor, player, true);
        } else {
            return selectColor(playerColor, player, false);
        }
    }

    public static boolean isGroupColorTheSame(String color, GroupColor groupColor) {
        return GroupColor.valueOf(color.toUpperCase()).equals(groupColor);
    }

}
