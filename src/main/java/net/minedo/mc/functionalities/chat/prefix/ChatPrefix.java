package net.minedo.mc.functionalities.chat.prefix;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.minedo.mc.functionalities.chat.ChatUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatPrefix implements Listener {

    @EventHandler
    public void onAsyncChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        event.renderer(((source, sourceDisplayName, message, viewer) -> Component.textOfChildren(
                ChatUtils.updateChatPrefixColor(player, sourceDisplayName),
                Component.text(String.format(":%s", StringUtils.SPACE)),
                message
        )));
    }

}
