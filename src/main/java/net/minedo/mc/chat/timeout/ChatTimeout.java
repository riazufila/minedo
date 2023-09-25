package net.minedo.mc.chat.timeout;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minedo.mc.Minedo;
import net.minedo.mc.constants.chattimeoutmessage.ChatTimeoutMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.UUID;

public class ChatTimeout implements Listener {

    private final HashMap<UUID, Integer> playerChatCount = new HashMap<>();
    private final Minedo pluginInstance;

    public ChatTimeout(Minedo pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        Integer chatCount = playerChatCount.get(player.getUniqueId());
        int CHAT_LIMIT = 15;

        if (chatCount == null) {
            new ChatTimeoutRelease(player, playerChatCount).runTaskLater(this.pluginInstance, 600);
        } else if (chatCount >= CHAT_LIMIT) {
            player.sendMessage(Component
                    .text(ChatTimeoutMessage.ERROR_CHAT_TIMEOUT.getMessage())
                    .color(NamedTextColor.RED)
            );

            event.setCancelled(true);
        }

        playerChatCount.put(player.getUniqueId(), chatCount == null ? 1 : chatCount + 1);
    }

}
