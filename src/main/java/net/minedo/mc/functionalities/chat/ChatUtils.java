package net.minedo.mc.functionalities.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minedo.mc.constants.command.type.colortype.ColorType;
import net.minedo.mc.constants.groupcolor.GroupColor;
import net.minedo.mc.constants.grouppermission.GroupPermission;
import net.minedo.mc.models.playercolor.PlayerColor;
import net.minedo.mc.repositories.playercolorrepository.PlayerColorRepository;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class ChatUtils {

    public static Component updateComponentColor(Player player, Component component, boolean isContent) {
        ChatInfo chatInfo = getString(player, isContent);
        String selectedColor = chatInfo.getSelectedColor();
        boolean isCustom = chatInfo.isCustom();

        if (!verifyPlayerPermissionForColorSetting(player, isCustom
                ? ColorType.CUSTOM.getType() : ColorType.PRESET.getType(), selectedColor)) {
            return component;
        }

        if (selectedColor != null) {
            if (isCustom) {
                component = component.color(TextColor.fromHexString(selectedColor));
            } else {
                component = component.color(GroupColor.valueOf(selectedColor).getColor());
            }
        } else {
            if (player.hasPermission(GroupPermission.OBSIDIAN.getPermission())) {
                component = component.color(GroupColor.OBSIDIAN.getColor());
            } else if (player.hasPermission(GroupPermission.REDSTONE.getPermission())) {
                component = component.color(GroupColor.REDSTONE.getColor());
            } else if (player.hasPermission(GroupPermission.DIAMOND.getPermission())) {
                component = component.color(GroupColor.DIAMOND.getColor());
            } else if (player.hasPermission(GroupPermission.EMERALD.getPermission())) {
                component = component.color(GroupColor.EMERALD.getColor());
            } else if (player.hasPermission(GroupPermission.GOLD.getPermission())) {
                component = component.color(GroupColor.GOLD.getColor());
            }
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

    private static boolean isGroupColorTheSame(String color, GroupColor groupColor) {
        return GroupColor.valueOf(color.toUpperCase()).equals(groupColor);
    }

    public static boolean verifyPlayerPermissionForColorSetting(Player player, String colorType, String color) {
        if (colorType.equals(ColorType.CUSTOM.getType())
                && !(player.hasPermission(GroupPermission.OBSIDIAN.getPermission()))
        ) {
            return false;
        } else if (colorType.equals(ColorType.PRESET.getType())) {
            return (!isGroupColorTheSame(color, GroupColor.OBSIDIAN)
                    || player.hasPermission(GroupPermission.OBSIDIAN.getPermission()))
                    && (!isGroupColorTheSame(color, GroupColor.REDSTONE)
                    || player.hasPermission(GroupPermission.REDSTONE.getPermission()))
                    && (!isGroupColorTheSame(color, GroupColor.DIAMOND)
                    || player.hasPermission(GroupPermission.DIAMOND.getPermission()))
                    && (!isGroupColorTheSame(color, GroupColor.EMERALD)
                    || player.hasPermission(GroupPermission.EMERALD.getPermission()))
                    && (!isGroupColorTheSame(color, GroupColor.GOLD)
                    || player.hasPermission(GroupPermission.GOLD.getPermission()));
        }

        return true;
    }

}
