package net.minedo.mc.repositories.playerblockedlistrepository;

import net.minedo.mc.models.playerblockedlist.PlayerBlockedList;
import net.minedo.mc.models.playerprofile.PlayerProfile;
import net.minedo.mc.repositories.Database;
import net.minedo.mc.repositories.playerprofilerepository.PlayerProfileRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class PlayerBlockedListRepository {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public List<PlayerBlockedList> getPlayerBlockedList(UUID playerUuid) {
        Database database = new Database();
        database.connect();

        PlayerProfileRepository playerProfileRepository = new PlayerProfileRepository();
        PlayerProfile playerProfile = playerProfileRepository.getPlayerProfileByUuid(playerUuid);

        List<PlayerBlockedList> playerBlockedList = new ArrayList<>();

        try {
            String query = """
                        SELECT * FROM player_blocked_list WHERE player_id = ?;
                    """;

            HashMap<Integer, String> replacements = new HashMap<>();
            replacements.put(1, String.valueOf(playerProfile.getId()));
            ResultSet resultSet = database.queryWithWhereClause(query, replacements);

            if (resultSet.next()) {
                int playerId = resultSet.getInt("player_id");
                int blockedPlayerId = resultSet.getInt("blocked_player_id");

                PlayerBlockedList playerBlocked = new PlayerBlockedList();
                playerBlocked.setPlayerId(playerId);
                playerBlocked.setBlockedPlayerId(blockedPlayerId);

                playerBlockedList.add(playerBlocked);
            }
        } catch (SQLException error) {
            this.logger.severe(String.format("Unable to get player blocked list: %s", error.getMessage()));
        } finally {
            database.disconnect();
        }

        return playerBlockedList;
    }

    public void addBlockedPlayer(UUID requesterUuid, UUID playerUuidToBeBlocked) {
        Database database = new Database();
        database.connect();

        PlayerProfileRepository requesterProfileRepository = new PlayerProfileRepository();
        PlayerProfile requesterProfile = requesterProfileRepository.getPlayerProfileByUuid(requesterUuid);

        PlayerProfileRepository playerToBeBlockedProfileRepository = new PlayerProfileRepository();
        PlayerProfile playerToBeBlockedProfile = playerToBeBlockedProfileRepository
                .getPlayerProfileByUuid(playerUuidToBeBlocked);

        String query = """
                    INSERT INTO player_blocked_list (player_id, blocked_player_id) VALUES (?, ?);
                """;

        HashMap<Integer, String> replacements = new HashMap<>();
        replacements.put(1, String.valueOf(requesterProfile.getId()));
        replacements.put(2, String.valueOf(playerToBeBlockedProfile.getId()));
        database.executeStatement(query, replacements);

        database.disconnect();
    }

    public void removeBlockedPlayer(UUID requesterUuid, UUID playerUuidToBeBlocked) {
        Database database = new Database();
        database.connect();

        PlayerProfileRepository requesterProfileRepository = new PlayerProfileRepository();
        PlayerProfile requesterProfile = requesterProfileRepository.getPlayerProfileByUuid(requesterUuid);

        PlayerProfileRepository playerToBeBlockedProfileRepository = new PlayerProfileRepository();
        PlayerProfile playerToBeBlockedProfile = playerToBeBlockedProfileRepository
                .getPlayerProfileByUuid(playerUuidToBeBlocked);

        String query = """
                    DELETE FROM player_blocked_list WHERE (player_id = ?) AND (blocked_player_id = ?);
                """;

        HashMap<Integer, String> replacements = new HashMap<>();
        replacements.put(1, String.valueOf(requesterProfile.getId()));
        replacements.put(2, String.valueOf(playerToBeBlockedProfile.getId()));
        database.executeStatement(query, replacements);

        database.disconnect();
    }

}
