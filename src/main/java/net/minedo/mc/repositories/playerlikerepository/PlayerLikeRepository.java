package net.minedo.mc.repositories.playerlikerepository;

import net.minedo.mc.repositories.Database;

import java.util.HashMap;

public class PlayerLikeRepository {

    public void insertNewPlayerLike(int playerId) {
        Database database = new Database();
        database.connect();

        String query = """
                    INSERT INTO player_like (player_id) VALUES (?);
                """;

        HashMap<Integer, String> replacements = new HashMap<>();
        replacements.put(1, String.valueOf(playerId));
        database.executeStatement(query, replacements);

        database.disconnect();
    }

}
