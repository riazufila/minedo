package net.minedo.mc.functionalities.chat.timeout;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minedo.mc.constants.command.message.chattimeoutmessage.ChatTimeoutMessage;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

/**
 * Chat timeout release.
 */
public class ChatTimeoutRelease extends BukkitRunnable {

    private final Player player;
    private final HashMap<UUID, Integer> playerChatCount;
    private final int chatLimit;

    /**
     * Initialize chat timeout release.
     *
     * @param player          player
     * @param playerChatCount player chat count
     * @param chatLimit       chat limit
     */
    public ChatTimeoutRelease(@NotNull Player player, @NotNull HashMap<UUID, Integer> playerChatCount, int chatLimit) {
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
