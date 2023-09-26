package net.minedo.mc.player;

import net.minedo.mc.models.playerprofile.PlayerProfile;
import net.minedo.mc.repositories.playerprofilerepository.PlayerProfileRepository;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerProfileManager implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        PlayerProfileRepository playerProfileRepository = new PlayerProfileRepository();
        PlayerProfile playerProfile = playerProfileRepository.getPlayerProfileByUuid(uuid);

        if (playerProfile == null) {
            playerProfileRepository.insertNewPlayerProfile(uuid);
        }
    }

}
