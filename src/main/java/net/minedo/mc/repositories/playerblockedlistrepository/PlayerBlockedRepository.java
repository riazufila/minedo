package net.minedo.mc.repositories.playerblockedlistrepository;

import net.minedo.mc.models.playerblockedlist.PlayerBlocked;
import net.minedo.mc.models.playerprofile.PlayerProfile;
import net.minedo.mc.repositories.Database;
import net.minedo.mc.repositories.playerprofilerepository.PlayerProfileRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Player blocked repository.
 */
public final class PlayerBlockedRepository {

    private static final Logger logger = Logger.getLogger(PlayerBlockedRepository.class.getName());

    /**
     * Get player block list.
     *
     * @param playerUuid player UUID
     * @return player block list
     */
    public static @Nullable List<PlayerBlocked> getPlayerBlockedList(@NotNull UUID playerUuid) {
        Database database = new Database();
        database.connect();

        List<PlayerBlocked> playerBlockedList = null;

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
                    if (playerBlockedList == null) {
                        playerBlockedList = new ArrayList<>();
                    }

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

    /**
     * Add player to a player's blocked list.
     *
     * @param requesterUuid         player UUID requesting to block
     * @param playerUuidToBeBlocked player UUID to be blocked
     */
    public static void addBlockedPlayer(@NotNull UUID requesterUuid, @NotNull UUID playerUuidToBeBlocked) {
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

    /**
     * Remove a player block a player's block list.
     *
     * @param requesterUuid         player UUID requesting to block
     * @param playerUuidToBeBlocked player UUID to be blocked
     */
    public static void removeBlockedPlayer(@NotNull UUID requesterUuid, @NotNull UUID playerUuidToBeBlocked) {
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

    /**
     * Get whether player is blocked by another player.
     *
     * @param requesterUuid        player UUID checking whether blocked or not
     * @param potentialBlockerUuid player UUID of a potential blocker
     * @return whether player is blocked by another player
     */
    public static boolean isPlayerBlockedByPlayer(@NotNull UUID requesterUuid, @NotNull UUID potentialBlockerUuid) {
        List<PlayerBlocked> playerBlockedList = PlayerBlockedRepository
                .getPlayerBlockedList(potentialBlockerUuid);

        if (playerBlockedList == null) {
            return false;
        }

        List<Integer> potentialBlockerPlayerBlockedList = playerBlockedList
                .stream()
                .map(PlayerBlocked::blockedPlayerId)
                .toList();

        PlayerProfile requesterPlayerProfile = PlayerProfileRepository.getPlayerProfileByUuid(requesterUuid);
        return potentialBlockerPlayerBlockedList.contains(requesterPlayerProfile.id());
    }

}
