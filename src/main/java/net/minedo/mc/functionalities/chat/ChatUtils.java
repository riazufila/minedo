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
        ChatInfo chatInfo = getString(player, isContent);
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

    private static ChatInfo getString(Player player, boolean isContent) {
        UUID playerUuid = player.getUniqueId();
        PlayerColorRepository playerColorRepository = new PlayerColorRepository();
        PlayerColor playerColor = playerColorRepository.getPlayerColorByPlayerUuid(playerUuid);
        boolean isCustom = false;
        String selectedColor;

        if (isContent) {
            if (playerColor.getContentCustom() != null) {
                selectedColor = playerColor.getContentCustom();
                isCustom = true;
            } else {
                selectedColor = playerColor.getContentPreset();
            }
        } else {
            if (playerColor.getPrefixCustom() != null) {
                selectedColor = playerColor.getPrefixCustom();
                isCustom = true;
            } else {
                selectedColor = playerColor.getPrefixPreset();
            }
        }

        return new ChatInfo(isCustom, selectedColor);
    }

    public static boolean isGroupColorTheSame(String color, GroupColor groupColor) {
        return GroupColor.valueOf(color.toUpperCase()).equals(groupColor);
    }

}
