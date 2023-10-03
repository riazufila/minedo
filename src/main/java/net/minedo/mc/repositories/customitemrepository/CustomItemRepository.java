package net.minedo.mc.repositories.customitemrepository;

import net.kyori.adventure.text.format.TextDecoration;
import net.minedo.mc.models.customitem.CustomItem;
import net.minedo.mc.models.customitemattribute.CustomItemAttribute;
import net.minedo.mc.models.customitemenchantment.CustomItemEnchantment;
import net.minedo.mc.models.customitemlore.CustomItemLore;
import net.minedo.mc.models.customitemprobability.CustomItemProbability;
import net.minedo.mc.repositories.Database;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class CustomItemRepository {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public List<CustomItem> getAllCustomItems() {
        Database database = new Database();
        database.connect();

        HashMap<Integer, CustomItem> customItemsMap = new HashMap<>();

        // Custom items must always have lore.
        try {
            String query = """
                        SELECT
                            customItem.id,
                            customItem.material,
                            customItem.display_name,
                            customItem.color,
                            customItem.decoration,
                            customItemLore.id AS custom_item_lore_id,
                            customItemLore.text AS custom_item_lore_text,
                            customItemLore.color AS custom_item_lore_color,
                            customItemLore.decoration AS custom_item_lore_decoration,
                            customItemLore.custom_item_id AS custom_item_lore_custom_item_id
                        FROM
                            custom_item customItem
                                INNER JOIN
                            custom_item_lore customItemLore ON customItem.id = customItemLore.custom_item_id
                        ORDER BY customItem.id;
                    """;

            ResultSet resultSet = database.query(query);

            while (resultSet.next()) {
                // Retrieve CustomItem.
                int id = resultSet.getInt("id");
                Material material = Material.valueOf(resultSet.getString("material"));
                String displayName = resultSet.getString("display_name");
                String color = resultSet.getString("color");
                String decoration = resultSet.getString("decoration");

                CustomItem customItem = customItemsMap.get(id);

                if (customItem == null) {
                    // Set CustomItem.
                    customItem = new CustomItem();
                    customItem.setId(id);
                    customItem.setMaterial(material);
                    customItem.setDisplayName(displayName);

                    if (color != null) {
                        customItem.setColor(color);
                    }

                    if (decoration != null) {
                        customItem.setDecoration(TextDecoration.valueOf(decoration));
                    }

                    customItemsMap.put(id, customItem);
                }

                // Retrieve CustomItemLore.
                int loreId = resultSet.getInt("custom_item_lore_id");
                String loreText = resultSet.getString("custom_item_lore_text");
                String loreColor = resultSet.getString("custom_item_lore_color");
                String loreDecoration = resultSet.getString("custom_item_lore_decoration");
                int loreCustomItemId = resultSet.getInt("custom_item_lore_custom_item_id");

                // Set CustomItemLore.
                CustomItemLore lore = new CustomItemLore();
                lore.setId(loreId);
                lore.setText(loreText);

                if (loreColor != null) {
                    lore.setColor(loreColor);
                }

                if (loreDecoration != null) {
                    lore.setDecoration(TextDecoration.valueOf(loreDecoration));
                }

                lore.setCustomItemId(loreCustomItemId);

                // Add the CustomItemLore to the CustomItem.
                customItem.setLore(lore);
            }

            // Custom Items may have enchantment(s).
            query = """
                        SELECT
                            customItem.id,
                            customItemEnchantment.id AS custom_item_enchantment_id,
                            customItemEnchantment.enchantment AS custom_item_enchantment_enchantment,
                            customItemEnchantment.level AS custom_item_enchantment_level,
                            customItemEnchantment.custom_item_id AS custom_item_enchantment_custom_item_id
                        FROM
                            custom_item customItem
                                INNER JOIN
                            custom_item_enchantment customItemEnchantment
                                ON customItem.id = customItemEnchantment.custom_item_id
                        ORDER BY customItem.id;
                    """;

            resultSet = database.query(query);

            while (resultSet.next()) {
                // Retrieve CustomItem.
                int id = resultSet.getInt("id");

                CustomItem customItem = customItemsMap.get(id);
                if (customItem.getEnchantments() == null) {
                    customItem.setEnchantments(new ArrayList<>()); // Initialize CustomItemEnchantment.
                }

                // Retrieve CustomItemEnchantment.
                int enchantmentId = resultSet.getInt("custom_item_enchantment_id");
                Enchantment enchantmentType = Enchantment.getByKey(
                        NamespacedKey.minecraft(resultSet.getString("custom_item_enchantment_enchantment"))
                );
                int enchantmentLevel = resultSet.getInt("custom_item_enchantment_level");
                int enchantmentCustomItemId = resultSet.getInt("custom_item_enchantment_custom_item_id");

                // Set CustomItemEnchantment.
                CustomItemEnchantment enchantment = new CustomItemEnchantment();
                enchantment.setId(enchantmentId);
                enchantment.setEnchantment(enchantmentType);
                enchantment.setLevel(enchantmentLevel);
                enchantment.setCustomItemId(enchantmentCustomItemId);

                // Add the CustomItemEnchantment to the CustomItem.
                customItem.addEnchantment(enchantment);
            }

            // Custom Items may have attribute(s).
            query = """
                        SELECT
                            customItem.id,
                            customItemAttribute.id AS custom_item_attribute_id,
                            customItemAttribute.attribute AS custom_item_attribute_attribute,
                            customItemAttribute.modifier AS custom_item_attribute_modifier,
                            customItemAttribute.operation AS custom_item_attribute_operation,
                            customItemAttribute.slot AS custom_item_attribute_slot,
                            customItemAttribute.custom_item_id AS custom_item_attribute_custom_item_id
                        FROM
                            custom_item customItem
                                INNER JOIN
                            custom_item_attribute customItemAttribute
                                ON customItem.id = customItemAttribute.custom_item_id
                        ORDER BY customItem.id;
                    """;

            resultSet = database.query(query);

            while (resultSet.next()) {
                // Retrieve CustomItem.
                int id = resultSet.getInt("id");

                CustomItem customItem = customItemsMap.get(id);
                if (customItem.getAttributes() == null) {
                    customItem.setAttributes(new ArrayList<>()); // Initialize CustomItemAttribute.
                }

                // Retrieve CustomItemAttribute.
                int attributeId = resultSet.getInt("custom_item_attribute_id");
                Attribute attributeType = Attribute.valueOf(
                        resultSet.getString("custom_item_attribute_attribute")
                );
                double attributeModifier = resultSet.getDouble("custom_item_attribute_modifier");
                AttributeModifier.Operation attributeOperation = AttributeModifier.Operation.valueOf(
                        resultSet.getString("custom_item_attribute_operation")
                );
                EquipmentSlot attributeSlot = EquipmentSlot.valueOf(
                        resultSet.getString("custom_item_attribute_slot")
                );
                int attributeCustomItemId = resultSet.getInt("custom_item_attribute_custom_item_id");

                // Set CustomItemAttribute.
                CustomItemAttribute attribute = new CustomItemAttribute();
                attribute.setId(attributeId);
                attribute.setAttribute(attributeType);
                attribute.setModifier(attributeModifier);
                attribute.setOperation(attributeOperation);
                attribute.setSlot(attributeSlot);
                attribute.setCustomItemId(attributeCustomItemId);

                // Add the CustomItemAttribute to the CustomItem.
                customItem.addAttribute(attribute);
            }

            // Custom Items must always have probability.
            query = """
                        SELECT
                            customItem.id, customItemProbability.probability
                        FROM
                            custom_item customItem
                                INNER JOIN
                            custom_item_probability customItemProbability
                                ON customItem.id = customItemProbability.custom_item_id
                        ORDER BY customItem.id;
                    """;

            resultSet = database.query(query);

            while (resultSet.next()) {
                // Retrieve CustomItem.
                int id = resultSet.getInt("id");

                CustomItem customItem = customItemsMap.get(id);

                // Retrieve CustomItemProbability.
                double probabilityAmount = resultSet.getInt("probability");

                // Set CustomItemProbability.
                CustomItemProbability probability = new CustomItemProbability();
                probability.setProbability(probabilityAmount);
                probability.setCustomItemId(id);

                // Add the CustomItemAttribute to the CustomItem.
                customItem.setProbability(probability);
            }
        } catch (SQLException e) {
            this.logger.severe(String.format("Unable to get custom items: %s", e.getMessage()));
        } finally {
            database.disconnect();
        }

        // Convert Map to Array.
        return List.of(customItemsMap.values().toArray(new CustomItem[0]));
    }

}
