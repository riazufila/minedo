package net.minedo.mc.chat.prefix;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatPrefix implements Listener {

    @EventHandler
    public void onAsyncChat(AsyncChatEvent event) {
        event.renderer(((source, sourceDisplayName, message, viewer) -> Component.textOfChildren(
                sourceDisplayName,
                Component.text(String.format(":%s", StringUtils.SPACE)),
                message
        )));
    }

}
