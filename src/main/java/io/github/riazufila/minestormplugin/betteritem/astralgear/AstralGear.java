package io.github.riazufila.minestormplugin.betteritem.astralgear;

import io.github.riazufila.minestormplugin.database.Database;
import net.kyori.adventure.text.format.TextDecoration;
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

public class AstralGear {

    private int id;
    private Material material;
    private String displayName;
    private String color;
    private TextDecoration decoration;
    private AstralGearLore lore;
    private List<AstralGearEnchantment> enchantments;
    private List<AstralGearAttribute> attributes;
    private AstralGearProbability probability;

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

    public AstralGearLore getLore() {
        return lore;
    }

    public void setLore(AstralGearLore lore) {
        this.lore = lore;
    }

    public List<AstralGearEnchantment> getEnchantments() {
        return enchantments;
    }

    public void setEnchantments(List<AstralGearEnchantment> enchantments) {
        this.enchantments = enchantments;
    }

    public List<AstralGearAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AstralGearAttribute> attributes) {
        this.attributes = attributes;
    }

    public AstralGearProbability getProbability() {
        return probability;
    }

    public void setProbability(AstralGearProbability probability) {
        this.probability = probability;
    }

    public static AstralGear[] getAllAstralGears() {
        Database database = new Database();
        database.connect();

        Map<Integer, AstralGear> astralGearsMap = new HashMap<>();

        // Astral Gears must always have lore.
        try {
            String query = """
                        SELECT
                            astralGear.id,
                            astralGear.material,
                            astralGear.display_name,
                            astralGear.color,
                            astralGear.decoration,
                            astralGearLore.id AS astral_gear_lore_id,
                            astralGearLore.text AS astral_gear_lore_text,
                            astralGearLore.color AS astral_gear_lore_color,
                            astralGearLore.decoration AS astral_gear_lore_decoration,
                            astralGearLore.astral_gear_id AS astral_gear_lore_astral_gear_id
                        FROM
                            astral_gear astralGear
                                INNER JOIN
                            astral_gear_lore astralGearLore ON astralGear.id = astralGearLore.astral_gear_id
                        ORDER BY astralGear.id;
                    """;

            ResultSet resultSet = database.query(query);

            while (resultSet.next()) {
                // Retrieve AstralGear.
                int id = resultSet.getInt("id");
                Material material = Material.valueOf(resultSet.getString("material"));
                String displayName = resultSet.getString("display_name");
                String color = resultSet.getString("color");
                String decoration = resultSet.getString("decoration");

                AstralGear astralGear = astralGearsMap.get(id);

                if (astralGear == null) {
                    // Set AstralGear.
                    astralGear = new AstralGear();
                    astralGear.setId(id);
                    astralGear.setMaterial(material);
                    astralGear.setDisplayName(displayName);

                    if (color != null) {
                        astralGear.setColor(color);
                    }

                    if (decoration != null) {
                        astralGear.setDecoration(TextDecoration.valueOf(decoration));
                    }

                    astralGearsMap.put(id, astralGear);
                }

                // Retrieve AstralGearLore.
                int loreId = resultSet.getInt("astral_gear_lore_id");
                String loreText = resultSet.getString("astral_gear_lore_text");
                String loreColor = resultSet.getString("astral_gear_lore_color");
                String loreDecoration = resultSet.getString("astral_gear_lore_decoration");
                int loreAstralGearId = resultSet.getInt("astral_gear_lore_astral_gear_id");

                // Set AstralGearLore.
                AstralGearLore lore = new AstralGearLore();
                lore.setId(loreId);
                lore.setText(loreText);

                if (loreColor != null) {
                    lore.setColor(loreColor);
                }

                if (loreDecoration != null) {
                    lore.setDecoration(TextDecoration.valueOf(loreDecoration));
                }

                lore.setAstralGearId(loreAstralGearId);

                // Add the AstralGearLore to the AstralGear
                astralGear.setLore(lore);
            }

            // Astral Gears may have enchantment(s).
            query = """
                        SELECT
                            astralGear.id,
                            astralGearEnchantment.id AS astral_gear_enchantment_id,
                            astralGearEnchantment.enchantment AS astral_gear_enchantment_enchantment,
                            astralGearEnchantment.level AS astral_gear_enchantment_level,
                            astralGearEnchantment.astral_gear_id AS astral_gear_enchantment_astral_gear_id
                        FROM
                            astral_gear astralGear
                                INNER JOIN
                            astral_gear_enchantment astralGearEnchantment ON astralGear.id = astralGearEnchantment.astral_gear_id
                        ORDER BY astralGear.id;
                    """;

            resultSet = database.query(query);

            while (resultSet.next()) {
                // Retrieve AstralGear.
                int id = resultSet.getInt("id");

                AstralGear astralGear = astralGearsMap.get(id);
                if (astralGear.enchantments == null) {
                    astralGear.setEnchantments(new ArrayList<>()); // Initialize AstralGearEnchantment.
                }

                // Retrieve AstralGearEnchantment.
                int enchantmentId = resultSet.getInt("astral_gear_enchantment_id");
                Enchantment enchantmentType = Enchantment.getByKey(NamespacedKey.minecraft(resultSet.getString("astral_gear_enchantment_enchantment")));
                int enchantmentLevel = resultSet.getInt("astral_gear_enchantment_level");
                int enchantmentAstralGearId = resultSet.getInt("astral_gear_enchantment_astral_gear_id");

                // Set AstralGearEnchantment.
                AstralGearEnchantment enchantment = new AstralGearEnchantment();
                enchantment.setId(enchantmentId);
                enchantment.setEnchantment(enchantmentType);
                enchantment.setLevel(enchantmentLevel);
                enchantment.setAstralGearId(enchantmentAstralGearId);

                // Add the AstralGearEnchantment to the AstralGear
                astralGear.enchantments.add(enchantment);
            }

            // Astral Gears may have attribute(s).
            query = """
                        SELECT
                            astralGear.id,
                            astralGearAttribute.id AS astral_gear_attribute_id,
                            astralGearAttribute.attribute AS astral_gear_attribute_attribute,
                            astralGearAttribute.modifier AS astral_gear_attribute_modifier,
                            astralGearAttribute.operation AS astral_gear_attribute_operation,
                            astralGearAttribute.slot AS astral_gear_attribute_slot,
                            astralGearAttribute.astral_gear_id AS astral_gear_attribute_astral_gear_id
                        FROM
                            astral_gear astralGear
                                INNER JOIN
                            astral_gear_attribute astralGearAttribute ON astralGear.id = astralGearAttribute.astral_gear_id
                        ORDER BY astralGear.id;
                    """;

            resultSet = database.query(query);

            while (resultSet.next()) {
                // Retrieve AstralGear.
                int id = resultSet.getInt("id");

                AstralGear astralGear = astralGearsMap.get(id);
                if (astralGear.attributes == null) {
                    astralGear.setAttributes(new ArrayList<>()); // Initialize AstralGearAttribute.
                }

                // Retrieve AstralGearAttribute.
                int attributeId = resultSet.getInt("astral_gear_attribute_id");
                Attribute attributeType = Attribute.valueOf(resultSet.getString("astral_gear_attribute_attribute"));
                double attributeModifier = resultSet.getDouble("astral_gear_attribute_modifier");
                AttributeModifier.Operation attributeOperation = AttributeModifier.Operation.valueOf(resultSet.getString("astral_gear_attribute_operation"));
                EquipmentSlot attributeSlot = EquipmentSlot.valueOf(resultSet.getString("astral_gear_attribute_slot"));
                int attributeAstralGearId = resultSet.getInt("astral_gear_attribute_astral_gear_id");

                // Set AstralGearAttribute.
                AstralGearAttribute attribute = new AstralGearAttribute();
                attribute.setId(attributeId);
                attribute.setAttribute(attributeType);
                attribute.setModifier(attributeModifier);
                attribute.setOperation(attributeOperation);
                attribute.setSlot(attributeSlot);
                attribute.setAstralGearId(attributeAstralGearId);

                // Add the AstralGearAttribute to the AstralGear
                astralGear.attributes.add(attribute);
            }

            // Astral Gears must always have probability.
            query = """
                        SELECT
                            astralGear.id, astralGearProbability.probability
                        FROM
                            astral_gear astralGear
                                INNER JOIN
                            astral_gear_probability astralGearProbability ON astralGear.id = astralGearProbability.astral_gear_id
                        ORDER BY astralGear.id;
                    """;

            resultSet = database.query(query);

            while (resultSet.next()) {
                // Retrieve AstralGear.
                int id = resultSet.getInt("id");

                AstralGear astralGear = astralGearsMap.get(id);

                // Retrieve AstralGearProbability.
                double probabilityAmount = resultSet.getInt("probability");

                // Set AstralGearProbability.
                AstralGearProbability probability = new AstralGearProbability();
                probability.setProbability(probabilityAmount);
                probability.setAstralGearId(id);

                // Add the AstralGearAttribute to the AstralGear
                astralGear.setProbability(probability);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            database.disconnect();
        }

        // Convert Map to Array.
        return astralGearsMap.values().toArray(new AstralGear[0]);
    }

}
