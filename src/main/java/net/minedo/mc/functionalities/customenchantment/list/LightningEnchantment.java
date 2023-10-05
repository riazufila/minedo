package net.minedo.mc.functionalities.customenchantment.list;

import net.minedo.mc.Minedo;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantment;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LightningEnchantment extends CustomEnchantment implements Listener {

    public LightningEnchantment(
            CustomEnchantmentType customEnchantmentType, short level, Minedo pluginInstance, String lore
    ) {
        super(customEnchantmentType, level, pluginInstance, lore);
    }

    @Override
    @EventHandler
    public void onHit(@NotNull EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            ItemStack itemAtHand = player.getInventory().getItemInMainHand();

            if (itemAtHand.isEmpty()) {
                return;
            }

            Minedo.getPlugin(Minedo.class);
            boolean isCustomEnchantmentExist = CustomEnchantmentUtils
                    .isCustomEnchantmentInItem(super.getPluginInstance(), itemAtHand, this.getCustomEnchantmentType());

            if (isCustomEnchantmentExist) {
                player.getWorld().strikeLightning(event.getEntity().getLocation());
            }
        }
    }

}
