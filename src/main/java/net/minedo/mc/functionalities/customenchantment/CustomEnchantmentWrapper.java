package net.minedo.mc.functionalities.customenchantment;

import net.minedo.mc.Minedo;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.list.*;
import net.minedo.mc.functionalities.dataembedder.DataEmbedder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomEnchantmentWrapper {

    public static Optional<CustomEnchantment> getCustomEnchantment(
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
            add(new WeaknessEnchantmentHandler());
            add(new WitherEnchantmentHandler());
        }};

        for (CustomEnchantmentHandler customEnchantmentHandler : customEnchantmentHandlers) {
            instance.getServer().getPluginManager().registerEvents(customEnchantmentHandler, instance);
        }
    }

}
