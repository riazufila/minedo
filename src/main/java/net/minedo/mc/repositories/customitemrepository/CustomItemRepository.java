package net.minedo.mc.repositories.customitemrepository;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minedo.mc.models.customitem.CustomItem;
import net.minedo.mc.models.customitemenchantment.CustomItemEnchantment;
import net.minedo.mc.models.customitemlore.CustomItemLore;
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

            Integer id = null;
            Material material = null;
            String displayName = null;
            NamedTextColor displayNameColor = null;
            TextDecoration displayNameDecoration = null;
            CustomItemLore customItemLore = null;
            List<CustomItemEnchantment> customItemEnchantments = null;

            while (resultSet.next()) {
                if (resultSet.getRow() == 1) {
                    id = resultSet.getInt("id");
                    material = Material.valueOf(resultSet.getString("material"));
                    displayName = resultSet.getString("display_name");
                    String unconvertedDisplayNameColor = resultSet.getString("display_name_color");
                    String unconvertedDisplayNameDecoration = resultSet.getString("display_name_decoration");


                    if (unconvertedDisplayNameColor != null) {
                        displayNameColor = NamedTextColor.NAMES.value(unconvertedDisplayNameColor.toLowerCase());
                    }

                    if (unconvertedDisplayNameDecoration != null) {
                        displayNameDecoration = TextDecoration
                                .valueOf(resultSet.getString("display_name_decoration"));
                    }

                    String lore = resultSet.getString("lore");
                    String unconvertedLoreColor = resultSet.getString("lore_color");
                    String unconvertedLoreDecoration = resultSet.getString("lore_decoration");

                    if (lore == null) {
                        continue;
                    }

                    NamedTextColor loreColor = null;
                    if (unconvertedLoreColor != null) {
                        loreColor = NamedTextColor.NAMES.value(unconvertedLoreColor.toLowerCase());
                    }

                    TextDecoration loreDecoration = null;
                    if (unconvertedLoreDecoration != null) {
                        loreDecoration = TextDecoration.valueOf(unconvertedLoreDecoration);
                    }

                    customItemLore = new CustomItemLore(lore, loreColor, loreDecoration);
                }

                String enchantment = resultSet.getString("enchantment");
                int enchantmentLevel = resultSet.getInt("enchantment_level");

                if (enchantment == null) {
                    continue;
                }

                CustomItemEnchantment customItemEnchantment = new CustomItemEnchantment(enchantment, enchantmentLevel);

                if (customItemEnchantments == null) {
                    customItemEnchantments = new ArrayList<>();
                }

                customItemEnchantments.add(customItemEnchantment);
            }

            if (id == null || displayName == null) {
                return null;
            }

            customItem = new CustomItem(id, material, displayName, displayNameColor, displayNameDecoration,
                    customItemLore, customItemEnchantments);
        } catch (SQLException exception) {
            logger.severe(String.format("Unable to get custom item by id: %s", exception.getMessage()));
        } finally {
            database.disconnect();
        }

        return customItem;
    }

}
