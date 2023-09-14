package net.minedo.mc.database.model.betteritem;

import net.kyori.adventure.text.format.TextDecoration;
import net.minedo.mc.database.Database;
import net.minedo.mc.database.model.betteritemattribute.BetterItemAttribute;
import net.minedo.mc.database.model.betteritemenchantment.BetterItemEnchantment;
import net.minedo.mc.database.model.betteritemlore.BetterItemLore;
import net.minedo.mc.database.model.betteritemprobability.BetterItemProbability;
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
import java.util.Map;
import java.util.logging.Logger;

public class BetterItem {

    private int id;
    private Material material;
    private String displayName;
    private String color;
    private TextDecoration decoration;
    private BetterItemLore lore;
    private List<BetterItemEnchantment> enchantments;
    private List<BetterItemAttribute> attributes;
    private BetterItemProbability probability;
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public BetterItem[] getAllBetterItems() {
        Database database = new Database();
        database.connect();

        Map<Integer, BetterItem> betterItemsMap = new HashMap<>();

        // Better Items must always have lore.
        try {
            String query = """
                        SELECT
                            betterItem.id,
                            betterItem.material,
                            betterItem.display_name,
                            betterItem.color,
                            betterItem.decoration,
                            betterItemLore.id AS better_item_lore_id,
                            betterItemLore.text AS better_item_lore_text,
                            betterItemLore.color AS better_item_lore_color,
                            betterItemLore.decoration AS better_item_lore_decoration,
                            betterItemLore.better_item_id AS better_item_lore_better_item_id
                        FROM
                            better_item betterItem
                                INNER JOIN
                            better_item_lore betterItemLore ON betterItem.id = betterItemLore.better_item_id
                        ORDER BY betterItem.id;
                    """;

            ResultSet resultSet = database.query(query);

            while (resultSet.next()) {
                // Retrieve BetterItem.
                int id = resultSet.getInt("id");
                Material material = Material.valueOf(resultSet.getString("material"));
                String displayName = resultSet.getString("display_name");
                String color = resultSet.getString("color");
                String decoration = resultSet.getString("decoration");

                BetterItem betterItem = betterItemsMap.get(id);

                if (betterItem == null) {
                    // Set BetterItem.
                    betterItem = new BetterItem();
                    betterItem.setId(id);
                    betterItem.setMaterial(material);
                    betterItem.setDisplayName(displayName);

                    if (color != null) {
                        betterItem.setColor(color);
                    }

                    if (decoration != null) {
                        betterItem.setDecoration(TextDecoration.valueOf(decoration));
                    }

                    betterItemsMap.put(id, betterItem);
                }

                // Retrieve BetterItemLore.
                int loreId = resultSet.getInt("better_item_lore_id");
                String loreText = resultSet.getString("better_item_lore_text");
                String loreColor = resultSet.getString("better_item_lore_color");
                String loreDecoration = resultSet.getString("better_item_lore_decoration");
                int loreBetterItemId = resultSet.getInt("better_item_lore_better_item_id");

                // Set BetterItemLore.
                BetterItemLore lore = new BetterItemLore();
                lore.setId(loreId);
                lore.setText(loreText);

                if (loreColor != null) {
                    lore.setColor(loreColor);
                }

                if (loreDecoration != null) {
                    lore.setDecoration(TextDecoration.valueOf(loreDecoration));
                }

                lore.setBetterItemId(loreBetterItemId);

                // Add the BetterItemLore to the BetterItem.
                betterItem.setLore(lore);
            }

            // Better Items may have enchantment(s).
            query = """
                        SELECT
                            betterItem.id,
                            betterItemEnchantment.id AS better_item_enchantment_id,
                            betterItemEnchantment.enchantment AS better_item_enchantment_enchantment,
                            betterItemEnchantment.level AS better_item_enchantment_level,
                            betterItemEnchantment.better_item_id AS better_item_enchantment_better_item_id
                        FROM
                            better_item betterItem
                                INNER JOIN
                            better_item_enchantment betterItemEnchantment ON betterItem.id = betterItemEnchantment.better_item_id
                        ORDER BY betterItem.id;
                    """;

            resultSet = database.query(query);

            while (resultSet.next()) {
                // Retrieve BetterItem.
                int id = resultSet.getInt("id");

                BetterItem betterItem = betterItemsMap.get(id);
                if (betterItem.enchantments == null) {
                    betterItem.setEnchantments(new ArrayList<>()); // Initialize BetterItemEnchantment.
                }

                // Retrieve BetterItemEnchantment.
                int enchantmentId = resultSet.getInt("better_item_enchantment_id");
                Enchantment enchantmentType = Enchantment.getByKey(NamespacedKey.minecraft(resultSet.getString("better_item_enchantment_enchantment")));
                int enchantmentLevel = resultSet.getInt("better_item_enchantment_level");
                int enchantmentBetterItemId = resultSet.getInt("better_item_enchantment_better_item_id");

                // Set BetterItemEnchantment.
                BetterItemEnchantment enchantment = new BetterItemEnchantment();
                enchantment.setId(enchantmentId);
                enchantment.setEnchantment(enchantmentType);
                enchantment.setLevel(enchantmentLevel);
                enchantment.setBetterItemId(enchantmentBetterItemId);

                // Add the BetterItemEnchantment to the BetterItem.
                betterItem.enchantments.add(enchantment);
            }

            // Better Items may have attribute(s).
            query = """
                        SELECT
                            betterItem.id,
                            betterItemAttribute.id AS better_item_attribute_id,
                            betterItemAttribute.attribute AS better_item_attribute_attribute,
                            betterItemAttribute.modifier AS better_item_attribute_modifier,
                            betterItemAttribute.operation AS better_item_attribute_operation,
                            betterItemAttribute.slot AS better_item_attribute_slot,
                            betterItemAttribute.better_item_id AS better_item_attribute_better_item_id
                        FROM
                            better_item betterItem
                                INNER JOIN
                            better_item_attribute betterItemAttribute ON betterItem.id = betterItemAttribute.better_item_id
                        ORDER BY betterItem.id;
                    """;

            resultSet = database.query(query);

            while (resultSet.next()) {
                // Retrieve BetterItem.
                int id = resultSet.getInt("id");

                BetterItem betterItem = betterItemsMap.get(id);
                if (betterItem.attributes == null) {
                    betterItem.setAttributes(new ArrayList<>()); // Initialize BetterItemAttribute.
                }

                // Retrieve BetterItemAttribute.
                int attributeId = resultSet.getInt("better_item_attribute_id");
                Attribute attributeType = Attribute.valueOf(resultSet.getString("better_item_attribute_attribute"));
                double attributeModifier = resultSet.getDouble("better_item_attribute_modifier");
                AttributeModifier.Operation attributeOperation = AttributeModifier.Operation.valueOf(resultSet.getString("better_item_attribute_operation"));
                EquipmentSlot attributeSlot = EquipmentSlot.valueOf(resultSet.getString("better_item_attribute_slot"));
                int attributeBetterItemId = resultSet.getInt("better_item_attribute_better_item_id");

                // Set BetterItemAttribute.
                BetterItemAttribute attribute = new BetterItemAttribute();
                attribute.setId(attributeId);
                attribute.setAttribute(attributeType);
                attribute.setModifier(attributeModifier);
                attribute.setOperation(attributeOperation);
                attribute.setSlot(attributeSlot);
                attribute.setBetterItemId(attributeBetterItemId);

                // Add the BetterItemAttribute to the BetterItem.
                betterItem.attributes.add(attribute);
            }

            // Better Items must always have probability.
            query = """
                        SELECT
                            betterItem.id, betterItemProbability.probability
                        FROM
                            better_item betterItem
                                INNER JOIN
                            better_item_probability betterItemProbability ON betterItem.id = betterItemProbability.better_item_id
                        ORDER BY betterItem.id;
                    """;

            resultSet = database.query(query);

            while (resultSet.next()) {
                // Retrieve BetterItem.
                int id = resultSet.getInt("id");

                BetterItem betterItem = betterItemsMap.get(id);

                // Retrieve BetterItemProbability.
                double probabilityAmount = resultSet.getInt("probability");

                // Set BetterItemProbability.
                BetterItemProbability probability = new BetterItemProbability();
                probability.setProbability(probabilityAmount);
                probability.setBetterItemId(id);

                // Add the BetterItemAttribute to the BetterItem.
                betterItem.setProbability(probability);
            }
        } catch (SQLException e) {
            this.logger.severe(String.format("Unable to get better items: %s", e.getMessage()));
        } finally {
            database.disconnect();
        }

        // Convert Map to Array.
        return betterItemsMap.values().toArray(new BetterItem[0]);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public TextDecoration getDecoration() {
        return decoration;
    }

    public void setDecoration(TextDecoration decoration) {
        this.decoration = decoration;
    }

    public BetterItemLore getLore() {
        return lore;
    }

    public void setLore(BetterItemLore lore) {
        this.lore = lore;
    }

    public List<BetterItemEnchantment> getEnchantments() {
        return enchantments;
    }

    public void setEnchantments(List<BetterItemEnchantment> enchantments) {
        this.enchantments = enchantments;
    }

    public List<BetterItemAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<BetterItemAttribute> attributes) {
        this.attributes = attributes;
    }

    public BetterItemProbability getProbability() {
        return probability;
    }

    public void setProbability(BetterItemProbability probability) {
        this.probability = probability;
    }

}
