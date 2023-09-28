package net.minedo.mc.functionalities.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
                component = component.color(NamedTextColor.DARK_PURPLE);
            } else if (player.hasPermission("minedo.group.redstone")) {
                component = component.color(NamedTextColor.DARK_RED);
            } else if (player.hasPermission("minedo.group.diamond")) {
                component = component.color(NamedTextColor.DARK_AQUA);
            } else if (player.hasPermission("minedo.group.emerald")) {
                component = component.color(NamedTextColor.GREEN);
            } else if (player.hasPermission("minedo.group.gold")) {
                component = component.color(NamedTextColor.GOLD);
            }
        }

        return component;
    }

}
