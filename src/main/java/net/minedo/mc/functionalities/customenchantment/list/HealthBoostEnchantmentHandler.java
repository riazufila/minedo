package net.minedo.mc.functionalities.customenchantment.list;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantment;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentHandler;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Optional;

public class HealthBoostEnchantmentHandler extends CustomEnchantmentHandler {

    public HealthBoostEnchantmentHandler() {
        super(CustomEnchantmentType.HEALTH_BOOST);
    }

    @Override
    @EventHandler
    public void onPlayerArmorChange(PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();
        ItemStack newItem = event.getNewItem();
        ItemStack oldItem = event.getOldItem();

        // Remove the custom enchantments of old item.
        Optional<CustomEnchantment> oldItemCustomEnchantmentOptional = CustomEnchantmentWrapper
                .getCustomEnchantment(oldItem, this.getCustomEnchantmentType());

        if (oldItemCustomEnchantmentOptional.isPresent()) {
            CustomEnchantment customEnchantment = oldItemCustomEnchantmentOptional.get();
            PotionEffect potionEffect = new PotionEffect(
                    PotionEffectType.HEAL,
                    PotionEffect.INFINITE_DURATION,
                    customEnchantment.getLevel() - 1
            );

            player.removePotionEffect(potionEffect.getType());
        }

        // Add the custom enchantments of new item.
        Optional<CustomEnchantment> newItemCustomEnchantmentOptional = CustomEnchantmentWrapper
                .getCustomEnchantment(newItem, this.getCustomEnchantmentType());

        if (newItemCustomEnchantmentOptional.isPresent()) {
            CustomEnchantment customEnchantment = newItemCustomEnchantmentOptional.get();
            PotionEffect potionEffect = new PotionEffect(
                    PotionEffectType.HEAL,
                    PotionEffect.INFINITE_DURATION,
                    customEnchantment.getLevel() - 1
            );

            player.addPotionEffect(potionEffect);
        }
    }

}
