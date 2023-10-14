package net.minedo.mc.functionalities.chat.box;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.minedo.mc.functionalities.chat.ChatUtils;
import net.minedo.mc.functionalities.utils.PermissionUtils;
import net.minedo.mc.models.playerprofile.PlayerProfile;
import net.minedo.mc.repositories.playerprofilerepository.PlayerProfileRepository;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * Chat box.
 */
public class ChatBox implements Listener {

    @EventHandler
    public void onAsyncChat(@NotNull AsyncChatEvent event) {
        Player player = event.getPlayer();
        PlayerProfile playerProfile = PlayerProfileRepository.getPlayerProfileByUuid(player.getUniqueId());

        event.renderer(((source, sourceDisplayName, message, viewer) -> {
            String nickname = playerProfile.nickname();
            Component nameComponent = sourceDisplayName;

            if (PermissionUtils.validatePlayerPermissionForNicknameDisplay(player) && nickname != null) {
                nameComponent = Component.text(nickname);
            }

            return Component.textOfChildren(
                    ChatUtils.updateComponentColor(player, nameComponent, false),
                    Component.text(String.format(":%s", StringUtils.SPACE)),
                    ChatUtils.updateComponentColor(player, message, true)
            );
        }));
    }

}
