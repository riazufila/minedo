package net.minedo.mc.functionalities.customenchantment;

import net.minedo.mc.Minedo;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.list.LightningEnchantmentHandler;
import net.minedo.mc.functionalities.customenchantment.list.PoisonEnchantmentHandler;
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
            add(new LightningEnchantmentHandler());
            add(new PoisonEnchantmentHandler());
        }};

        for (CustomEnchantmentHandler customEnchantmentHandler : customEnchantmentHandlers) {
            instance.getServer().getPluginManager().registerEvents(customEnchantmentHandler, instance);
        }
    }

}
