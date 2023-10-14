package net.minedo.mc.functionalities.customenchantment;

import com.destroystokyo.paper.MaterialTags;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import net.minedo.mc.constants.common.Common;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.customevents.PlayerNonBlockInteractEvent;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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
     * Get item used for the skill.
     *
     * @param event event
     * @return item used for the skill
     */
    public @Nullable ItemStack getValidItemForSkill(@NotNull PlayerNonBlockInteractEvent event) {
        Player player = event.getPlayer();
        EntityEquipment equipment = player.getEquipment();
        EquipmentSlot handUsed = event.getHand();
        ItemStack itemUsed = event.getItem();
        ItemStack itemInOtherHand = null;

        if (handUsed == EquipmentSlot.HAND) {
            itemInOtherHand = equipment.getItemInOffHand();
        } else if (handUsed == EquipmentSlot.OFF_HAND) {
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
     * Get whether hit is valid.
     *
     * @param event event
     * @return whether hit is valid
     */
    private @Nullable CombatEvent isOnHitValid(@NotNull EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity defendingEntity)) {
            return null;
        }

        if (!(event.getDamager() instanceof LivingEntity attackingEntity)) {
            return null;
        }


        if (attackingEntity.getEquipment() == null) {
            return null;
        }

        ItemStack itemInMainHand = attackingEntity.getEquipment().getItemInMainHand();
        if (itemInMainHand.isEmpty()) {
            return null;
        }

        Material material = itemInMainHand.getType();
        if (!(MaterialTags.SWORDS.isTagged(material) || Tag.ITEMS_TOOLS.isTagged(material))) {
            return null;
        }

        if (attackingEntity instanceof Player attackingPlayer) {
            if (attackingPlayer.getAttackCooldown() != (float) Common.ATTACK_COOL_DOWN.getValue()) {
                return null;
            }
        }

        return new CombatEvent(itemInMainHand, attackingEntity, defendingEntity, null);
    }

    /**
     * Get potion effect for on hit events.
     *
     * @param potionEffectType  potion effect type
     * @param isAmplified       whether potion effect can be amplified
     * @param customEnchantment custom enchantment
     * @return potion effect
     */
    private @NotNull PotionEffect getPotionEffect(
            @NotNull PotionEffectType potionEffectType,
            boolean isAmplified,
            @NotNull CustomEnchantment customEnchantment
    ) {
        final int DEFAULT_DURATION = 3;
        final double INCREMENT_DURATION = 0.2;
        int enchantmentLevel = customEnchantment.getLevel();
        int duration = isAmplified
                ? (int) (DEFAULT_DURATION + (enchantmentLevel * INCREMENT_DURATION))
                : DEFAULT_DURATION * enchantmentLevel;
        int amplifier = isAmplified ? enchantmentLevel : 0;

        return new PotionEffect(
                potionEffectType,
                duration * (int) Common.TICK_PER_SECOND.getValue(),
                Math.min(amplifier, this.AMPLIFIER_LIMIT) // Limit maximum potion effect amplifier.
        );
    }

    /**
     * Check whether player is able to inflict custom enchantment on hit.
     *
     * @param event event
     * @return whether player is able to inflict custom enchantment on hit
     */
    public CombatEvent isAbleToInflictCustomEnchantmentHits(@NotNull EntityDamageByEntityEvent event) {
        CombatEvent combatEvent = this.isOnHitValid(event);

        if (combatEvent == null) {
            return null;
        }

        Optional<CustomEnchantment> customEnchantmentOptional = CustomEnchantmentWrapper
                .getCustomEnchantment(combatEvent.getItem(), this.getCustomEnchantmentType());

        if (customEnchantmentOptional.isEmpty()) {
            return null;
        }

        combatEvent.setCustomEnchantment(customEnchantmentOptional.get());

        return combatEvent;
    }

    /**
     * Trigger custom effects on hit.
     *
     * @param event            event
     * @param potionEffectType potion effect type as in {@link PotionEffectType#values()}
     * @param isAmplified      whether potion effect can be amplified
     */
    public void triggerCustomEffectsOnHit(
            @NotNull EntityDamageByEntityEvent event, @NotNull PotionEffectType potionEffectType, boolean isAmplified
    ) {
        CombatEvent combatEvent = this.isAbleToInflictCustomEnchantmentHits(event);

        if (combatEvent == null || combatEvent.getCustomEnchantment() == null) {
            return;
        }

        CustomEnchantment customEnchantment = combatEvent.getCustomEnchantment();
        PotionEffect potionEffect = this.getPotionEffect(potionEffectType, isAmplified, customEnchantment);
        combatEvent.getDefendingEntity().addPotionEffect(potionEffect);
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
        int amplifier = customEnchantment.getLevel() - 1;

        if (potionEffects.containsKey(potionEffectType)) {
            PotionEffect existingPotionEffect = potionEffects.get(potionEffectType);
            amplifier = amplifier + existingPotionEffect.getAmplifier();
        }

        PotionEffect potionEffect = new PotionEffect(
                potionEffectType,
                PotionEffect.INFINITE_DURATION,
                Math.min(amplifier, this.AMPLIFIER_LIMIT) // Limit maximum potion effect amplifier.
        );

        potionEffects.put(potionEffectType, potionEffect);
    }

    /**
     * Update custom effects on armor change.
     *
     * @param event            event
     * @param potionEffectType potion effect type as in {@link PotionEffectType#values()}
     */
    public void updateCustomEffectsOnArmorChange(
            @NotNull PlayerArmorChangeEvent event,
            @NotNull PotionEffectType potionEffectType
    ) {
        Inventory inventory = event.getPlayer().getInventory();
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

        HumanEntity player = event.getPlayer();

        player.removePotionEffect(potionEffectType);
        player.addPotionEffects(potionEffects.values());
    }

}
