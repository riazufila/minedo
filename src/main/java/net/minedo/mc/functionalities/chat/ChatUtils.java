package net.minedo.mc.functionalities.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minedo.mc.constants.chatcolorsymbol.ChatColorSymbol;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.intellij.lang.annotations.RegExp;

public final class ChatUtils {

    /**
     * Updated given component according to permission's color and symbol trigger used.
     * Do not use this to update component that have texts prepended to the actual message,
     * if your intention is to color and sanitize the text based on permission and symbol
     * trigger.
     *
     * @param player    Player
     * @param component Component
     * @return Component
     */
    public static Component updateChatContentColor(Player player, Component component) {
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

    public static Component setChatColorAndCombineText(
            Player player, String actualText, String appendedText, String format
    ) {
        char firstCharacter = actualText.charAt(0);
        NamedTextColor namedTextColor = null;
        boolean isUpdated = false;
        Component component;
        String updatedText;

        if (firstCharacter == ChatColorSymbol.PURPLE_CHAT_SYMBOL.getSymbol()
                && player.hasPermission("minedo.group.obsidian")) {
            namedTextColor = NamedTextColor.DARK_PURPLE;
            isUpdated = true;
        } else if (firstCharacter == ChatColorSymbol.RED_CHAT_SYMBOL.getSymbol()
                && player.hasPermission("minedo.group.redstone")) {
            namedTextColor = NamedTextColor.DARK_RED;
            isUpdated = true;
        } else if (firstCharacter == ChatColorSymbol.TURQUOISE_CHAT_SYMBOL.getSymbol()
                && player.hasPermission("minedo.group.diamond")) {
            namedTextColor = NamedTextColor.DARK_AQUA;
            isUpdated = true;
        } else if (firstCharacter == ChatColorSymbol.GREEN_CHAT_SYMBOL.getSymbol()
                && player.hasPermission("minedo.group.emerald")) {
            namedTextColor = NamedTextColor.GREEN;
            isUpdated = true;
        } else if (firstCharacter == ChatColorSymbol.GOLD_CHAT_SYMBOL.getSymbol()
                && player.hasPermission("minedo.group.gold")) {
            namedTextColor = NamedTextColor.GOLD;
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

            updatedText = String.format(
                    format,
                    actualText.replaceFirst(REGEX_COLOURED_CHAT_SYMBOL, StringUtils.EMPTY),
                    appendedText
            );
        } else {
            updatedText = String.format(
                    format,
                    actualText,
                    appendedText
            );
        }

        if (namedTextColor != null) {
            component = Component.text(updatedText).color(namedTextColor);
        } else {
            component = Component.text(updatedText);
        }

        return component;
    }

    public static Component updateChatPrefixColor(Player player, Component component) {
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

        return component;
    }

}
