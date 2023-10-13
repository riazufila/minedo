package net.minedo.mc.functionalities.customenchantment;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Combat event information
 */
public class CombatEvent {

    private final ItemStack item;
    private final LivingEntity attackingEntity;
    private final LivingEntity defendingEntity;
    private CustomEnchantment customEnchantment;

    /**
     * Combat event information
     *
     * @param item              item used for the combat
     * @param attackingEntity   entity that attacked
     * @param defendingEntity   entity that defended
     * @param customEnchantment custom enchantment
     */
    public CombatEvent(
            @NotNull ItemStack item, @NotNull LivingEntity attackingEntity,
            @NotNull LivingEntity defendingEntity, @Nullable CustomEnchantment customEnchantment
    ) {
        this.item = item;
        this.attackingEntity = attackingEntity;
        this.defendingEntity = defendingEntity;
        this.customEnchantment = customEnchantment;
    }

    public @NotNull ItemStack getItem() {
        return item;
    }

    public @NotNull LivingEntity getAttackingEntity() {
        return attackingEntity;
    }

    public @NotNull LivingEntity getDefendingEntity() {
        return defendingEntity;
    }

    public @Nullable CustomEnchantment getCustomEnchantment() {
        return customEnchantment;
    }

    public void setCustomEnchantment(@NotNull CustomEnchantment customEnchantment) {
        this.customEnchantment = customEnchantment;
    }

}
