package net.minedo.mc.functionalities.customenchantment;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public record CombatEvent(ItemStack item, LivingEntity attackingEntity, LivingEntity defendingEntity) {
}
