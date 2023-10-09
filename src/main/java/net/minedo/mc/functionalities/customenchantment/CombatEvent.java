package net.minedo.mc.functionalities.customenchantment;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Combat event information
 *
 * @param item            item used for the combat
 * @param attackingEntity entity that attacked
 * @param defendingEntity entity that defended
 */
public record CombatEvent(@NotNull ItemStack item,
                          @NotNull LivingEntity attackingEntity,
                          @NotNull LivingEntity defendingEntity) {
}
