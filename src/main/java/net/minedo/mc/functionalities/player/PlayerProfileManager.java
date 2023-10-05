package net.minedo.mc.functionalities.player;

import net.minedo.mc.models.playercolor.PlayerColor;
import net.minedo.mc.models.playerlike.PlayerLike;
import net.minedo.mc.models.playerprofile.PlayerProfile;
import net.minedo.mc.repositories.playercolorrepository.PlayerColorRepository;
import net.minedo.mc.repositories.playerlikerepository.PlayerLikeRepository;
import net.minedo.mc.repositories.playerprofilerepository.PlayerProfileRepository;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerProfileManager implements Listener {

    private void initializePlayerProfile(UUID playerUuid) {
        PlayerProfile playerProfile = PlayerProfileRepository.getPlayerProfileByUuid(playerUuid);

        if (playerProfile == null) {
            PlayerProfileRepository.insertNewPlayerProfile(playerUuid);
        }

    }

    private void initializePlayerColor(UUID playerUuid) {
        PlayerColor playerColor = PlayerColorRepository.getPlayerColorByPlayerUuid(playerUuid);

        if (playerColor == null) {
            PlayerColorRepository.insertNewPlayerColor(playerUuid);
        }
    }

    private void initializePlayerLike(UUID playerUuid) {
        PlayerLike playerLike = PlayerLikeRepository.getPlayerLikeByPlayerUuid(playerUuid);

        if (playerLike == null) {
            PlayerLikeRepository.insertNewPlayerLike(playerUuid);
        }
    }

    private void initializePlayer(UUID playerUuid) {
        this.initializePlayerProfile(playerUuid);
        this.initializePlayerColor(playerUuid);
        this.initializePlayerLike(playerUuid);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        this.initializePlayer(player.getUniqueId());
    }

}
