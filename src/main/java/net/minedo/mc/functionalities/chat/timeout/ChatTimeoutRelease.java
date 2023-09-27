package net.minedo.mc.functionalities.chat.timeout;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minedo.mc.constants.chattimeoutmessage.ChatTimeoutMessage;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class ChatTimeoutRelease extends BukkitRunnable {

    private final Player player;
    private final HashMap<UUID, Integer> playerChatCount;
    private final int chatLimit;

    public ChatTimeoutRelease(Player player, HashMap<UUID, Integer> playerChatCount, int chatLimit) {
        this.player = player;
        this.playerChatCount = playerChatCount;
        this.chatLimit = chatLimit;
    }

    @Override
    public void run() {
        Integer chatCount = playerChatCount.get(player.getUniqueId());

        playerChatCount.remove(this.player.getUniqueId());

        if (chatCount > chatLimit) {
            player.sendMessage(Component
                    .text(ChatTimeoutMessage.INFO_CHAT_ENABLED.getMessage())
                    .color(NamedTextColor.GREEN)
            );
        }
    }

}
