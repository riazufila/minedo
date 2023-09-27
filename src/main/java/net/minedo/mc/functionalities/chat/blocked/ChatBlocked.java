package net.minedo.mc.functionalities.chat.blocked;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.minedo.mc.repositories.playerblockedlistrepository.PlayerBlockedRepository;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.UUID;

public class ChatBlocked implements Listener {

    @EventHandler
    public void onAsyncChat(AsyncChatEvent event) {
        HashSet<Audience> audiences = new HashSet<>(event.viewers());

        for (Audience audience : audiences) {
            if (audience instanceof Player player) {
                UUID playerUuid = player.getUniqueId();
                PlayerBlockedRepository playerBlockedRepository = new PlayerBlockedRepository();

                if (playerBlockedRepository.isPlayerBlockedByPlayer(
                        event.getPlayer().getUniqueId(), playerUuid)) {
                    event.viewers().remove(player);
                }
            }
        }
    }

}
