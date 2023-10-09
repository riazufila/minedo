package net.minedo.mc.functionalities.player;

import net.minedo.mc.repositories.playercolorrepository.PlayerColorRepository;
import net.minedo.mc.repositories.playerlikerepository.PlayerLikeRepository;
import net.minedo.mc.repositories.playerprofilerepository.PlayerProfileRepository;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

/**
 * Player profile manager.
 */
public class PlayerProfileManager implements Listener {

    /**
     * Initialize player that hasn't played before.
     *
     * @param player player
     */
    private void initializePlayer(Player player) {
        if (!player.hasPlayedBefore()) {
            UUID playerUuid = player.getUniqueId();

            PlayerProfileRepository.insertNewPlayerProfile(playerUuid);
            PlayerColorRepository.insertNewPlayerColor(playerUuid);
            PlayerLikeRepository.insertNewPlayerLike(playerUuid);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        this.initializePlayer(player);
    }

}
