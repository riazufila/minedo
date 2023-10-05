package net.minedo.mc.functionalities.customenchantment;

import net.minedo.mc.Minedo;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.list.LightningEnchantment;

import java.util.ArrayList;
import java.util.List;

public class CustomEnchantmentManager {

    private final List<CustomEnchantment> customEnchantments = new ArrayList<>();

    public CustomEnchantmentManager() {
        // TODO: Make a database call and loop through all the custom enchants and add accordingly.
        this.customEnchantments.add(new LightningEnchantment(
                CustomEnchantmentType.LIGHTNING, (short) 1, "Summons lightning to target."
        ));
    }

    public void registerCustomEnchantments() {
        Minedo instance = Minedo.getInstance();

        for (CustomEnchantment customEnchantment : this.customEnchantments) {
            instance.getServer().getPluginManager().registerEvents(customEnchantment, instance);
        }
    }

}
