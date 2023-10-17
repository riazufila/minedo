package net.minedo.mc.functionalities.customenchantment;

import com.destroystokyo.paper.MaterialTags;
import net.minedo.mc.constants.common.Common;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.helper.CombatData;
import net.minedo.mc.functionalities.skills.SkillUtils;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Custom enchantment handler abstract class. Custom enchantments should all extends from this.
 */
public abstract class CustomEnchantmentHandler extends SimpleCustomEnchantment {

    private final int AMPLIFIER_LIMIT = 9;

    /**
     * Custom enchantment handler initializer.
     *
     * @param customEnchantmentType custom enchantment type as in {@link CustomEnchantmentType#values()}
     */
    public CustomEnchantmentHandler(@NotNull CustomEnchantmentType customEnchantmentType) {
        super(customEnchantmentType);
    }

    /**
     * Get valid item used for the skill.
     *
     * @param player        player
     * @param equipmentSlot equipment slot
     * @param itemUsed      item used
     * @return valid item used for the skill
     */
    private @Nullable ItemStack getValidItemForSkill(
            @NotNull Player player, @Nullable EquipmentSlot equipmentSlot, @Nullable ItemStack itemUsed
    ) {
        EntityEquipment equipment = player.getEquipment();
        ItemStack itemInOtherHand = null;

        if (equipmentSlot == EquipmentSlot.HAND) {
            itemInOtherHand = equipment.getItemInOffHand();
        } else if (equipmentSlot == EquipmentSlot.OFF_HAND) {
            itemInOtherHand = equipment.getItemInMainHand();
        }

        if ((itemUsed == null || itemUsed.isEmpty())
                || (itemInOtherHand == null || itemInOtherHand.isEmpty())
        ) {
            return null;
        }

        if (MaterialTags.ARMOR.isTagged(itemUsed.getType())) {
            return null;
        }

        Optional<CustomEnchantment> customEnchantmentOptional = CustomEnchantmentWrapper
                .getCustomEnchantment(itemInOtherHand, CustomEnchantmentType.CATALYST);

        if (customEnchantmentOptional.isEmpty()) {
            return null;
        }


        return itemUsed;
    }

    /**
     * Get combat data on hit
     *
     * @param defendingEntity defending entity
     * @param attackingEntity attacking entity
     * @return combat data on hit
     */
    private @Nullable CombatData getCombatDataOnHit(
            @NotNull Entity defendingEntity, @NotNull Entity attackingEntity
    ) {
        if (!(defendingEntity instanceof LivingEntity defendingLivingEntity)) {
            return null;
        }

        if (!(attackingEntity instanceof LivingEntity attackingLivingEntity)) {
            return null;
        }


        if (attackingLivingEntity.getEquipment() == null) {
            return null;
        }

        ItemStack itemInMainHand = attackingLivingEntity.getEquipment().getItemInMainHand();
        if (itemInMainHand.isEmpty()) {
            return null;
        }

        Material material = itemInMainHand.getType();
        if (!(MaterialTags.SWORDS.isTagged(material) || Tag.ITEMS_TOOLS.isTagged(material))) {
            return null;
        }

        if (attackingLivingEntity instanceof Player attackingPlayer) {
            if (attackingPlayer.getAttackCooldown() != (float) Common.ATTACK_COOL_DOWN.getValue()) {
                return null;
            }
        }

        return new CombatData(itemInMainHand, attackingLivingEntity, defendingLivingEntity, null);
    }

    /**
     * Check whether player is able to inflict custom enchantment on hit.
     *
     * @param defendingEntity defending entity
     * @param attackingEntity attacking entity
     * @return whether player is able to inflict custom enchantment on hit
     */
    public CombatData isAbleToInflictCustomEnchantmentOnHit(
            @NotNull Entity defendingEntity, @NotNull Entity attackingEntity
    ) {
        CombatData combatData = this.getCombatDataOnHit(defendingEntity, attackingEntity);

        if (combatData == null) {
            return null;
        }

        Optional<CustomEnchantment> customEnchantmentOptional = CustomEnchantmentWrapper
                .getCustomEnchantment(combatData.getItem(), this.getCustomEnchantmentType());

        if (customEnchantmentOptional.isEmpty()) {
            return null;
        }

        combatData.setCustomEnchantment(customEnchantmentOptional.get());

        return combatData;
    }

    /**
     * Get potion effect on hit.
     *
     * @param customEnchantment custom enchantment
     * @param potionEffectType  potion effect type
     * @param isAmplified       whether potion effect is amplified
     * @return potion effect
     */
    public PotionEffect getPotionEffectOnHit(
            @NotNull CustomEnchantment customEnchantment,
            @NotNull PotionEffectType potionEffectType,
            boolean isAmplified
    ) {

        final int DEFAULT_DURATION = 3;
        final double INCREMENT_DURATION = 0.2;
        int enchantmentLevel = customEnchantment.getLevel();
        int duration = isAmplified
                ? (int) (DEFAULT_DURATION + (enchantmentLevel * INCREMENT_DURATION))
                : DEFAULT_DURATION * enchantmentLevel;
        int amplifier = isAmplified ? enchantmentLevel - 1 : 0;

        return new PotionEffect(
                potionEffectType,
                duration * (int) Common.TICK_PER_SECOND.getValue(),
                Math.min(amplifier, this.AMPLIFIER_LIMIT) // Limit maximum potion effect amplifier.
        );
    }

    /**
     * Trigger potion effects on hit.
     *
     * @param defendingEntity  defending entity
     * @param attackingEntity  attacking entity
     * @param potionEffectType potion effect type as in {@link PotionEffectType#values()}
     * @param isAmplified      whether potion effect can be amplified
     */
    public void triggerPotionEffectsOnHit(
            @NotNull Entity defendingEntity,
            @NotNull Entity attackingEntity,
            @NotNull PotionEffectType potionEffectType,
            boolean isAmplified
    ) {
        CombatData combatData = this.isAbleToInflictCustomEnchantmentOnHit(defendingEntity, attackingEntity);

        if (combatData == null || combatData.getCustomEnchantment() == null) {
            return;
        }

        CustomEnchantment customEnchantment = combatData.getCustomEnchantment();
        PotionEffect potionEffect = this.getPotionEffectOnHit(customEnchantment, potionEffectType, isAmplified);
        combatData.getDefendingEntity().addPotionEffect(potionEffect);
    }

    /**
     * Get potion effect on hit.
     *
     * @param customEnchantment custom enchantment
     * @param potionEffectType  potion effect type
     * @param potionEffects     potion effects
     * @return potion effect
     */
    public PotionEffect getPotionEffectOnArmorChange(
            @NotNull CustomEnchantment customEnchantment,
            @NotNull PotionEffectType potionEffectType,
            @NotNull HashMap<PotionEffectType, @NotNull PotionEffect> potionEffects

    ) {
        int amplifier = customEnchantment.getLevel() - 1;

        if (potionEffects.containsKey(potionEffectType)) {
            PotionEffect existingPotionEffect = potionEffects.get(potionEffectType);
            amplifier = amplifier + existingPotionEffect.getAmplifier();
        }

        return new PotionEffect(
                potionEffectType,
                PotionEffect.INFINITE_DURATION,
                Math.min(amplifier, this.AMPLIFIER_LIMIT) // Limit maximum potion effect amplifier.
        );
    }

    /**
     * Update potion effects based on item.
     *
     * @param potionEffects    potion effects
     * @param item             item
     * @param potionEffectType potion effect type as in {@link PotionEffectType#values()}
     */
    private void updatePotionEffectsBasedOnItem(
            @NotNull HashMap<PotionEffectType, @NotNull PotionEffect> potionEffects,
            @Nullable ItemStack item, @NotNull PotionEffectType potionEffectType
    ) {
        if (item == null || item.isEmpty()) {
            return;
        }

        Optional<CustomEnchantment> customEnchantmentOptional = CustomEnchantmentWrapper
                .getCustomEnchantment(item, this.getCustomEnchantmentType());

        if (customEnchantmentOptional.isEmpty()) {
            return;
        }

        CustomEnchantment customEnchantment = customEnchantmentOptional.get();
        PotionEffect potionEffect = this.getPotionEffectOnArmorChange(
                customEnchantment, potionEffectType, potionEffects
        );

        potionEffects.put(potionEffectType, potionEffect);
    }

    /**
     * Update potion effects on armor change.
     *
     * @param player           player
     * @param potionEffectType potion effect type as in {@link PotionEffectType#values()}
     */
    public void updatePotionEffectsOnArmorChange(
            @NotNull Player player,
            @NotNull PotionEffectType potionEffectType
    ) {
        Inventory inventory = player.getInventory();
        int HEAD_SLOT = 39;
        int CHEST_SLOT = 38;
        int LEGS_SLOT = 37;
        int BOOTS_SLOT = 36;
        List<ItemStack> itemsAtArmorSlot = new ArrayList<>() {{
            add(inventory.getItem(HEAD_SLOT));
            add(inventory.getItem(CHEST_SLOT));
            add(inventory.getItem(LEGS_SLOT));
            add(inventory.getItem(BOOTS_SLOT));
        }};

        HashMap<PotionEffectType, PotionEffect> potionEffects = new HashMap<>();

        for (ItemStack item : itemsAtArmorSlot) {
            this.updatePotionEffectsBasedOnItem(potionEffects, item, potionEffectType);
        }

        player.removePotionEffect(potionEffectType);
        player.addPotionEffects(potionEffects.values());
    }

    /**
     * Get custom enchantment if player is able to use skill.
     *
     * @param player            player
     * @param equipmentSlot     equipment slot
     * @param itemUsed          item used
     * @param playerSkillPoints player skill points
     * @return custom enchantment
     */
    public CustomEnchantment getCustomEnchantmentOnSKill(
            @NotNull Player player,
            @Nullable EquipmentSlot equipmentSlot,
            @Nullable ItemStack itemUsed,
            @NotNull HashMap<UUID, Integer> playerSkillPoints
    ) {
        ItemStack item = this.getValidItemForSkill(player, equipmentSlot, itemUsed);

        if (item == null) {
            return null;
        }

        Optional<CustomEnchantment> customEnchantmentOptional = CustomEnchantmentWrapper
                .getCustomEnchantment(item, this.getCustomEnchantmentType());

        if (customEnchantmentOptional.isEmpty()) {
            return null;
        }

        if (!SkillUtils.canSkill(player, playerSkillPoints)) {
            return null;
        }

        return customEnchantmentOptional.get();
    }

}
