package net.minedo.mc.repositories.customitemprobabilityrepository;

import net.minedo.mc.models.customitemprobability.CustomItemProbability;
import net.minedo.mc.repositories.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class CustomItemProbabilityRepository {

    private static final Logger logger = Logger.getLogger(CustomItemProbabilityRepository.class.getName());

    public static List<CustomItemProbability> getAllCustomItemsProbabilities() {
        Database database = new Database();
        database.connect();

        List<CustomItemProbability> customItemProbabilities = new ArrayList<>();

        try {
            String query = """
                        SELECT
                            custom_item_id, probability
                        FROM
                            custom_item_probability;
                    """;

            try (ResultSet resultSet = database.query(query)) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("custom_item_id");
                    double probability = resultSet.getDouble("probability");

                    CustomItemProbability customItemProbability = new CustomItemProbability(id, probability);
                    customItemProbabilities.add(customItemProbability);
                }
            }
        } catch (SQLException exception) {
            logger.severe(String.format("Unable to get custom items probabilities: %s", exception.getMessage()));
        } finally {
            database.disconnect();
        }

        return customItemProbabilities;
    }

}
