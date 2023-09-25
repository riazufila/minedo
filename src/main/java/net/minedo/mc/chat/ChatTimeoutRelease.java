package net.minedo.mc.chat;

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

    public ChatTimeoutRelease(Player player, HashMap<UUID, Integer> playerChatCount) {
        this.player = player;
        this.playerChatCount = playerChatCount;
    }

    @Override
    public void run() {
        playerChatCount.remove(this.player.getUniqueId());
        player.sendMessage(Component
                .text(ChatTimeoutMessage.INFO_CHAT_ENABLED.getMessage())
                .color(NamedTextColor.GREEN)
        );
    }

}
