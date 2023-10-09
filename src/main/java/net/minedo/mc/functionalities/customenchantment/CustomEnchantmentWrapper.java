package net.minedo.mc.functionalities.customenchantment;

import net.minedo.mc.Minedo;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.list.*;
import net.minedo.mc.functionalities.dataembedder.DataEmbedder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Custom enchantment wrapper. Handles custom enchantment handler initializer.
 */
public class CustomEnchantmentWrapper {

    /**
     * Get custom enchantment.
     *
     * @param item                  item
     * @param customEnchantmentType custom enchantment type as in {@link CustomEnchantmentType#values()}
     * @return custom enchantment
     */
    public static @NotNull Optional<CustomEnchantment> getCustomEnchantment(
            ItemStack item, CustomEnchantmentType customEnchantmentType
    ) {
        List<CustomEnchantment> customEnchantments = DataEmbedder.getCustomEnchantments(item);

        if (customEnchantments != null && !customEnchantments.isEmpty()) {
            return customEnchantments
                    .stream()
                    .filter(enchantment -> enchantment
                            .getCustomEnchantmentType()
                            .equals(customEnchantmentType))
                    .findFirst();
        }

        return Optional.empty();
    }

    /**
     * Format custom enchantment name.
     *
     * @param enchantmentName enchantment name based on {@link CustomEnchantmentType#values()}
     * @return formatted custom enchantment name
     */
    public static @NotNull String formatCustomEnchantmentName(String enchantmentName) {
        String[] words = enchantmentName.split("_");

        StringBuilder formattedName = new StringBuilder();

        // Iterate through the words and capitalize the first letter of each word
        for (String word : words) {
            // Append a space if the formattedName is not empty
            if (!formattedName.isEmpty()) {
                formattedName.append(" ");
            }

            // Append the word with the first letter capitalized
            formattedName.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1).toLowerCase());
        }

        return formattedName.toString();
    }

    /**
     * Register the listeners for all custom enchantments.
     */
    public void registerCustomEnchantments() {
        Minedo instance = Minedo.getInstance();
        List<CustomEnchantmentHandler> customEnchantmentHandlers = new ArrayList<>() {{
            add(new BlindnessEnchantmentHandler());
            add(new ConfusionEnchantmentHandler());
            add(new DarknessEnchantmentHandler());
            add(new GlowingEnchantmentHandler());
            add(new HarmEnchantmentHandler());
            add(new HealEnchantmentHandler());
            add(new HealthBoostEnchantmentHandler());
            add(new HungerEnchantmentHandler());
            add(new LightningEnchantmentHandler());
            add(new PoisonEnchantmentHandler());
            add(new RegenerationEnchantmentHandler());
            add(new SlowEnchantmentHandler());
            add(new SpeedEnchantmentHandler());
            add(new WeaknessEnchantmentHandler());
            add(new WitherEnchantmentHandler());
        }};

        for (CustomEnchantmentHandler customEnchantmentHandler : customEnchantmentHandlers) {
            instance.getServer().getPluginManager().registerEvents(customEnchantmentHandler, instance);
        }
    }

}
