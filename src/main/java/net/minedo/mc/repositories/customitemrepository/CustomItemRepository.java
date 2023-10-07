package net.minedo.mc.repositories.customitemrepository;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minedo.mc.models.customitem.CustomItem;
import net.minedo.mc.models.customitemenchantment.CustomItemEnchantment;
import net.minedo.mc.models.customitemlore.CustomItemLore;
import net.minedo.mc.models.customitemprobability.CustomItemProbability;
import net.minedo.mc.repositories.Database;
import org.bukkit.Material;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public final class CustomItemRepository {

    private static final Logger logger = Logger.getLogger(CustomItemRepository.class.getName());

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

            ResultSet resultSet = database.query(query);

            while (resultSet.next()) {
                int id = resultSet.getInt("custom_item_id");
                double probability = resultSet.getDouble("probability");

                CustomItemProbability customItemProbability = new CustomItemProbability();
                customItemProbability.setCustomItemId(id);
                customItemProbability.setProbability(probability);

                customItemProbabilities.add(customItemProbability);
            }
        } catch (SQLException exception) {
            logger.severe(String.format("Unable to get custom items probabilities: %s", exception.getMessage()));
        } finally {
            database.disconnect();
        }

        return customItemProbabilities;
    }

    public static CustomItem getCustomItemById(int customItemId) {
        Database database = new Database();
        database.connect();

        CustomItem customItem = null;

        try {
            String query = """
                        SELECT
                            custom_item.id,
                            custom_item.material,
                            custom_item.display_name,
                            custom_item.color AS display_name_color,
                            custom_item.decoration AS display_name_decoration,
                            custom_item_lore.text AS lore,
                            custom_item_lore.color AS lore_color,
                            custom_item_lore.decoration AS lore_decoration,
                            custom_item_enchantment.enchantment AS enchantment,
                            custom_item_enchantment.level AS enchantment_level
                        FROM
                            custom_item
                                LEFT JOIN
                            custom_item_lore ON custom_item_lore.custom_item_id = custom_item.id
                                LEFT JOIN
                            custom_item_enchantment ON custom_item_enchantment.custom_item_id = custom_item.id
                        WHERE
                            custom_item.id = ?;
                    """;

            HashMap<Integer, String> replacements = new HashMap<>();
            replacements.put(1, String.valueOf(customItemId));
            ResultSet resultSet = database.queryWithWhereClause(query, replacements);

            while (resultSet.next()) {
                if (customItem == null) {
                    customItem = new CustomItem();

                    int id = resultSet.getInt("id");
                    Material material = Material.valueOf(resultSet.getString("material"));
                    String displayName = resultSet.getString("display_name");
                    String unconvertedDisplayNameColor = resultSet.getString("display_name_color");
                    String unconvertedDisplayNameDecoration = resultSet.getString("display_name_decoration");

                    customItem.setId(id);
                    customItem.setMaterial(material);
                    customItem.setDisplayName(displayName);

                    if (unconvertedDisplayNameColor != null) {
                        NamedTextColor displayNameColor = NamedTextColor.NAMES
                                .value(unconvertedDisplayNameColor.toLowerCase());
                        customItem.setColor(displayNameColor);
                    }

                    if (unconvertedDisplayNameDecoration != null) {
                        TextDecoration displayNameDecoration = TextDecoration
                                .valueOf(resultSet.getString("display_name_decoration"));
                        customItem.setDecoration(displayNameDecoration);
                    }

                    CustomItemLore customItemLore = new CustomItemLore();

                    String lore = resultSet.getString("lore");
                    String unconvertedLoreColor = resultSet.getString("lore_color");
                    String unconvertedLoreDecoration = resultSet.getString("lore_decoration");

                    customItemLore.setText(lore);

                    if (unconvertedLoreColor != null) {
                        NamedTextColor loreColor = NamedTextColor.NAMES.value(unconvertedLoreColor.toLowerCase());
                        customItemLore.setColor(loreColor);
                    }

                    if (unconvertedLoreDecoration != null) {
                        TextDecoration loreDecoration = TextDecoration.valueOf(unconvertedLoreDecoration);
                        customItemLore.setDecoration(loreDecoration);
                    }

                    customItem.setLore(customItemLore);
                }

                CustomItemEnchantment customItemEnchantment = new CustomItemEnchantment();

                String enchantment = resultSet.getString("enchantment");
                int enchantmentLevel = resultSet.getInt("enchantment_level");

                customItemEnchantment.setEnchantment(enchantment);
                customItemEnchantment.setLevel(enchantmentLevel);
                customItem.addEnchantment(customItemEnchantment);
            }
        } catch (SQLException exception) {
            logger.severe(String.format("Unable to get custom item by id: %s", exception.getMessage()));
        } finally {
            database.disconnect();
        }

        return customItem;
    }

}
