package net.minedo.mc.functionalities.customenchantment;

import net.minedo.mc.Minedo;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.list.LightningEnchantment;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class CustomEnchantmentManager implements Listener {

    private final Minedo pluginInstance;
    private final List<CustomEnchantment> customEnchantments = new ArrayList<>();

    public CustomEnchantmentManager(Minedo pluginInstance) {
        this.pluginInstance = pluginInstance;

        // TODO: Make a database call and loop through all the custom enchants and add accordingly.
        this.customEnchantments.add(new LightningEnchantment(
                CustomEnchantmentType.LIGHTNING, (short) 1, this.pluginInstance, "Summons lightning to target."
        ));
    }

    public void registerEvents() {
        for (CustomEnchantment customEnchantment : this.customEnchantments) {
            this.pluginInstance.getServer().getPluginManager().registerEvents(customEnchantment, this.pluginInstance);
        }
    }

}
