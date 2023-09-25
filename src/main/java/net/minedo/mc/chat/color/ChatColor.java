package net.minedo.mc.chat.color;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minedo.mc.constants.chatcolorsymbol.ChatColorSymbol;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatColor implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        Component component = event.message();
        String plainText = PlainTextComponentSerializer.plainText().serialize(component);
        char firstCharacter = plainText.charAt(0);

        if (firstCharacter == ChatColorSymbol.PURPLE_CHAT_SYMBOL.getSymbol()
                && player.hasPermission("minedo.group.obsidian")) {
            component = component.color(NamedTextColor.DARK_PURPLE);
        } else if (firstCharacter == ChatColorSymbol.RED_CHAT_SYMBOL.getSymbol()
                && player.hasPermission("minedo.group.redstone")) {
            component = component.color(NamedTextColor.DARK_RED);
        } else if (firstCharacter == ChatColorSymbol.TURQUOISE_CHAT_SYMBOL.getSymbol()
                && player.hasPermission("minedo.group.diamond")) {
            component = component.color(NamedTextColor.DARK_AQUA);
        } else if (firstCharacter == ChatColorSymbol.GREEN_CHAT_SYMBOL.getSymbol()
                && player.hasPermission("minedo.group.emerald")) {
            component = component.color(NamedTextColor.GREEN);
        } else if (firstCharacter == ChatColorSymbol.GOLD_CHAT_SYMBOL.getSymbol()
                && player.hasPermission("minedo.group.gold")) {
            component = component.color(NamedTextColor.GOLD);
        }

        event.message(component);
    }

}
