package net.minedo.mc.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minedo.mc.constants.chatcolorsymbol.ChatColorSymbol;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.intellij.lang.annotations.RegExp;

public final class ChatUtils {

    public static Component updateChatColor(Player player, Component component) {
        String plainText = PlainTextComponentSerializer.plainText().serialize(component);
        char firstCharacter = plainText.charAt(0);
        boolean isUpdated = false;

        if (firstCharacter == ChatColorSymbol.PURPLE_CHAT_SYMBOL.getSymbol()
                && player.hasPermission("minedo.group.obsidian")) {
            component = component.color(NamedTextColor.DARK_PURPLE);
            isUpdated = true;
        } else if (firstCharacter == ChatColorSymbol.RED_CHAT_SYMBOL.getSymbol()
                && player.hasPermission("minedo.group.redstone")) {
            component = component.color(NamedTextColor.DARK_RED);
            isUpdated = true;
        } else if (firstCharacter == ChatColorSymbol.TURQUOISE_CHAT_SYMBOL.getSymbol()
                && player.hasPermission("minedo.group.diamond")) {
            component = component.color(NamedTextColor.DARK_AQUA);
            isUpdated = true;
        } else if (firstCharacter == ChatColorSymbol.GREEN_CHAT_SYMBOL.getSymbol()
                && player.hasPermission("minedo.group.emerald")) {
            component = component.color(NamedTextColor.GREEN);
            isUpdated = true;
        } else if (firstCharacter == ChatColorSymbol.GOLD_CHAT_SYMBOL.getSymbol()
                && player.hasPermission("minedo.group.gold")) {
            component = component.color(NamedTextColor.GOLD);
            isUpdated = true;
        }

        if (isUpdated) {
            StringBuilder chatColorSymbolJoined = new StringBuilder(StringUtils.EMPTY);

            for (ChatColorSymbol symbol : ChatColorSymbol.values()) {
                chatColorSymbolJoined.append(symbol.getSymbol());
            }

            @RegExp String REGEX_COLOURED_CHAT_SYMBOL = String.format(
                    "^[ ]*([%s])[ ]*", chatColorSymbolJoined
            );

            TextReplacementConfig replacement = TextReplacementConfig.builder()
                    .match(REGEX_COLOURED_CHAT_SYMBOL)
                    .replacement(Component.text(StringUtils.EMPTY))
                    .once()
                    .build();

            component = component.replaceText(replacement);
        }

        return component;
    }

}
