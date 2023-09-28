package net.minedo.mc.functionalities.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minedo.mc.constants.groupcolor.GroupColor;
import net.minedo.mc.models.playercolor.PlayerColor;
import net.minedo.mc.repositories.playercolorrepository.PlayerColorRepository;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class ChatUtils {

    public static Component updateComponentColor(Player player, Component component, boolean isContent) {
        UUID playerUuid = player.getUniqueId();
        PlayerColorRepository playerColorRepository = new PlayerColorRepository();
        PlayerColor playerColor = playerColorRepository.getPlayerColorByPlayerUuid(playerUuid);
        String selectedColor = isContent ? playerColor.getContentPreset() : playerColor.getPrefixPreset();

        if (selectedColor != null) {
            component = component.color(NamedTextColor.NAMES.value(selectedColor));
        } else {
            if (player.hasPermission("minedo.group.obsidian")) {
                component = component.color(GroupColor.OBSIDIAN.getColor());
            } else if (player.hasPermission("minedo.group.redstone")) {
                component = component.color(GroupColor.REDSTONE.getColor());
            } else if (player.hasPermission("minedo.group.diamond")) {
                component = component.color(GroupColor.DIAMOND.getColor());
            } else if (player.hasPermission("minedo.group.emerald")) {
                component = component.color(GroupColor.EMERALD.getColor());
            } else if (player.hasPermission("minedo.group.gold")) {
                component = component.color(GroupColor.GOLD.getColor());
            }
        }

        return component;
    }

}
