package net.minedo.mc.repositories.playerhomerepository;

import net.minedo.mc.models.playerhome.PlayerHome;
import net.minedo.mc.models.playerprofile.PlayerProfile;
import net.minedo.mc.repositories.Database;
import net.minedo.mc.repositories.playerprofilerepository.PlayerProfileRepository;
import org.bukkit.Location;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class PlayerHomeRepository {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public PlayerHome getPlayerHome(UUID playerUuid, String name) {
        Database database = new Database();
        database.connect();

        PlayerProfileRepository playerProfileRepository = new PlayerProfileRepository();
        PlayerProfile playerProfile = playerProfileRepository.getPlayerProfileByUuid(playerUuid);
        PlayerHome playerHome = null;

        try {
            String query = """
                        SELECT * FROM player_home WHERE player_id = ? AND name = ?;
                    """;

            HashMap<Integer, String> replacements = new HashMap<>();
            replacements.put(1, String.valueOf(playerProfile.getId()));
            replacements.put(2, name);
            ResultSet resultSet = database.queryWithWhereClause(query, replacements);

            if (resultSet.next()) {
                int playerId = resultSet.getInt("player_id");
                String homeName = resultSet.getString("name");
                double coordinateX = resultSet.getDouble("coordinate_x");
                double coordinateY = resultSet.getDouble("coordinate_y");
                double coordinateZ = resultSet.getDouble("coordinate_z");

                playerHome = new PlayerHome();
                playerHome.setPlayerId(playerId);
                playerHome.setName(homeName);
                playerHome.setCoordinateX(coordinateX);
                playerHome.setCoordinateY(coordinateY);
                playerHome.setCoordinateZ(coordinateZ);
            }
        } catch (SQLException error) {
            this.logger.severe(String.format(
                    "Unable to get player home by player id and name: %s", error.getMessage()
            ));
        } finally {
            database.disconnect();
        }

        return playerHome;
    }

    public List<PlayerHome> getPlayerHomeList(UUID playerUuid) {
        Database database = new Database();
        database.connect();

        PlayerProfileRepository playerProfileRepository = new PlayerProfileRepository();
        PlayerProfile playerProfile = playerProfileRepository.getPlayerProfileByUuid(playerUuid);

        List<PlayerHome> playerHomeList = new ArrayList<>();

        try {
            String query = """
                        SELECT * FROM player_home WHERE player_id = ?;
                    """;

            HashMap<Integer, String> replacements = new HashMap<>();
            replacements.put(1, String.valueOf(playerProfile.getId()));
            ResultSet resultSet = database.queryWithWhereClause(query, replacements);

            while (resultSet.next()) {
                int playerId = resultSet.getInt("player_id");
                String homeName = resultSet.getString("name");
                double coordinateX = resultSet.getDouble("coordinate_x");
                double coordinateY = resultSet.getDouble("coordinate_y");
                double coordinateZ = resultSet.getDouble("coordinate_z");

                PlayerHome playerHome = new PlayerHome();
                playerHome.setPlayerId(playerId);
                playerHome.setName(homeName);
                playerHome.setCoordinateX(coordinateX);
                playerHome.setCoordinateY(coordinateY);
                playerHome.setCoordinateZ(coordinateZ);

                playerHomeList.add(playerHome);
            }
        } catch (SQLException error) {
            this.logger.severe(String.format("Unable to get player home: %s", error.getMessage()));
        } finally {
            database.disconnect();
        }

        return playerHomeList;
    }

    public void upsertHome(UUID playerUuid, Location location, String homeName) {
        Database database = new Database();
        database.connect();

        PlayerProfileRepository playerProfileRepository = new PlayerProfileRepository();
        PlayerProfile playerProfile = playerProfileRepository.getPlayerProfileByUuid(playerUuid);

        String query = """
                    REPLACE INTO player_home
                        (player_id, name, coordinate_x, coordinate_y, coordinate_z)
                    VALUES
                        (?, ?, ?, ?, ?);
                """;

        HashMap<Integer, String> replacements = new HashMap<>();
        replacements.put(1, String.valueOf(playerProfile.getId()));
        replacements.put(2, homeName);
        replacements.put(3, String.valueOf(location.getX()));
        replacements.put(4, String.valueOf(location.getY()));
        replacements.put(5, String.valueOf(location.getZ()));
        database.executeStatement(query, replacements);

        database.disconnect();
    }

    public void removeHome(UUID playerUuid, String homeName) {
        Database database = new Database();
        database.connect();

        PlayerProfileRepository playerProfileRepository = new PlayerProfileRepository();
        PlayerProfile playerProfile = playerProfileRepository.getPlayerProfileByUuid(playerUuid);

        String query = """
                    DELETE FROM player_home WHERE (player_id = ?) AND (name = ?);
                """;

        HashMap<Integer, String> replacements = new HashMap<>();
        replacements.put(1, String.valueOf(playerProfile.getId()));
        replacements.put(2, homeName);
        database.executeStatement(query, replacements);

        database.disconnect();
    }

}
