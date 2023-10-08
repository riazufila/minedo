package net.minedo.mc.repositories.playerblockedlistrepository;

import net.minedo.mc.models.playerblockedlist.PlayerBlocked;
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

public final class PlayerBlockedRepository {

    private static final Logger logger = Logger.getLogger(PlayerBlockedRepository.class.getName());

    public static List<PlayerBlocked> getPlayerBlockedList(UUID playerUuid) {
        Database database = new Database();
        database.connect();

        List<PlayerBlocked> playerBlockedList = new ArrayList<>();

        try {
            String query = """
                        SELECT
                            blocked_player_id
                        FROM
                            player_blocked
                        WHERE
                            player_id = (SELECT id FROM player_profile WHERE uuid = ?);
                    """;

            HashMap<Integer, String> replacements = new HashMap<>();
            replacements.put(1, String.valueOf(playerUuid));

            try (ResultSet resultSet = database.queryWithWhereClause(query, replacements)) {
                while (resultSet.next()) {
                    int blockedPlayerId = resultSet.getInt("blocked_player_id");

                    PlayerBlocked playerBlocked = new PlayerBlocked(blockedPlayerId);
                    playerBlockedList.add(playerBlocked);
                }
            }
        } catch (SQLException error) {
            logger.severe(String.format("Unable to get player blocked: %s", error.getMessage()));
        } finally {
            database.disconnect();
        }

        return playerBlockedList;
    }

    public static void addBlockedPlayer(UUID requesterUuid, UUID playerUuidToBeBlocked) {
        Database database = new Database();
        database.connect();

        PlayerProfile requesterProfile = PlayerProfileRepository.getPlayerProfileByUuid(requesterUuid);
        PlayerProfile playerToBeBlockedProfile = PlayerProfileRepository.getPlayerProfileByUuid(playerUuidToBeBlocked);

        String query = """
                    INSERT INTO player_blocked (player_id, blocked_player_id) VALUES (?, ?);
                """;

        HashMap<Integer, String> replacements = new HashMap<>();
        replacements.put(1, String.valueOf(requesterProfile.id()));
        replacements.put(2, String.valueOf(playerToBeBlockedProfile.id()));
        database.executeStatement(query, replacements);

        database.disconnect();
    }

    public static void removeBlockedPlayer(UUID requesterUuid, UUID playerUuidToBeBlocked) {
        Database database = new Database();
        database.connect();

        PlayerProfile requesterProfile = PlayerProfileRepository.getPlayerProfileByUuid(requesterUuid);
        PlayerProfile playerToBeBlockedProfile = PlayerProfileRepository.getPlayerProfileByUuid(playerUuidToBeBlocked);

        String query = """
                    DELETE FROM player_blocked WHERE (player_id = ?) AND (blocked_player_id = ?);
                """;

        HashMap<Integer, String> replacements = new HashMap<>();
        replacements.put(1, String.valueOf(requesterProfile.id()));
        replacements.put(2, String.valueOf(playerToBeBlockedProfile.id()));
        database.executeStatement(query, replacements);

        database.disconnect();
    }

    public static boolean isPlayerBlockedByPlayer(UUID requesterUuid, UUID potentialBlockerUuid) {
        List<Integer> potentialBlockerPlayerBlockedList = PlayerBlockedRepository
                .getPlayerBlockedList(potentialBlockerUuid)
                .stream()
                .map(PlayerBlocked::blockedPlayerId)
                .toList();

        PlayerProfile requesterPlayerProfile = PlayerProfileRepository.getPlayerProfileByUuid(requesterUuid);
        return potentialBlockerPlayerBlockedList.contains(requesterPlayerProfile.id());
    }

}
