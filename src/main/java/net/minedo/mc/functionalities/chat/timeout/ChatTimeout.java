package net.minedo.mc.functionalities.chat.timeout;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minedo.mc.Minedo;
import net.minedo.mc.constants.command.message.chattimeoutmessage.ChatTimeoutMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

/**
 * Chat timeout.
 */
public class ChatTimeout implements Listener {

    private final HashMap<UUID, Integer> playerChatCount = new HashMap<>();

    @EventHandler
    public void onAsyncChat(@NotNull AsyncChatEvent event) {
        Player player = event.getPlayer();
        Integer chatCount = playerChatCount.get(player.getUniqueId());
        int CHAT_LIMIT = 15;

        if (chatCount == null) {
            new ChatTimeoutRelease(player, playerChatCount, CHAT_LIMIT).runTaskLater(Minedo.getInstance(), 600);
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
