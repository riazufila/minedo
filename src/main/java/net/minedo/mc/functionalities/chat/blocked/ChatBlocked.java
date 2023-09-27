package net.minedo.mc.functionalities.chat.blocked;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.minedo.mc.models.playerblockedlist.PlayerBlocked;
import net.minedo.mc.models.playerprofile.PlayerProfile;
import net.minedo.mc.repositories.playerblockedlistrepository.PlayerBlockedRepository;
import net.minedo.mc.repositories.playerprofilerepository.PlayerProfileRepository;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class ChatBlocked implements Listener {

    @EventHandler
    public void onAsyncChat(AsyncChatEvent event) {
        HashSet<Audience> audiences = new HashSet<>(event.viewers());
        PlayerProfileRepository playerProfileRepository = new PlayerProfileRepository();
        PlayerProfile playerProfile = playerProfileRepository.getPlayerProfileByUuid(
                event.getPlayer().getUniqueId()
        );

        for (Audience audience : audiences) {
            if (audience instanceof Player player) {
                UUID playerUuid = player.getUniqueId();

                PlayerBlockedRepository playerBlockedRepository = new PlayerBlockedRepository();
                List<Integer> playerBlockedList = playerBlockedRepository
                        .getPlayerBlockedList(playerUuid)
                        .stream()
                        .map(PlayerBlocked::getBlockedPlayerId)
                        .toList();

                if (playerBlockedList.contains(playerProfile.getId())) {
                    event.viewers().remove(player);
                }
            }
        }
    }

}
