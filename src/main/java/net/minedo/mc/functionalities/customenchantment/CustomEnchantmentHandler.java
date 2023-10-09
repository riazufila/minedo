package net.minedo.mc.functionalities.customenchantment;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import net.minedo.mc.constants.common.Common;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Custom enchantment handler abstract class. Custom enchantments should all extends from this.
 */
public abstract class CustomEnchantmentHandler extends SimpleCustomEnchantment implements Listener {

    private final int AMPLIFIER_LIMIT = 9;

    /**
     * Custom enchantment handler initializer.
     *
     * @param customEnchantmentType custom enchantment type as in {@link CustomEnchantmentType#values()}
     */
    public CustomEnchantmentHandler(CustomEnchantmentType customEnchantmentType) {
        super(customEnchantmentType);
    }

    /**
     * Get whether hit is valid.
     *
     * @param event event
     * @return whether hit is valid
     */
    public @Nullable CombatEvent isOnHitValid(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity defendingEntity)) {
            return null;
        }

        if (!(event.getDamager() instanceof LivingEntity attackingEntity)) {
            return null;
        }


        if (attackingEntity.getEquipment() == null) {
            return null;
        }

        ItemStack itemAtHand = attackingEntity.getEquipment().getItemInMainHand();

        if (itemAtHand.isEmpty()) {
            return null;
        }

        if (attackingEntity instanceof Player attackingPlayer) {
            if (attackingPlayer.getAttackCooldown() != (float) Common.ATTACK_COOL_DOWN.getValue()) {
                return null;
            }
        }

        return new CombatEvent(itemAtHand, attackingEntity, defendingEntity);
    }

    /**
     * Trigger custom effects on hit.
     *
     * @param event            event
     * @param potionEffectType potion effect type as in {@link PotionEffectType#values()}
     * @param isAmplified      whether potion effect can be amplified
     */
    public void triggerCustomEffectsOnHit(
            EntityDamageByEntityEvent event, PotionEffectType potionEffectType, boolean isAmplified
    ) {
        CombatEvent combatEvent = this.isOnHitValid(event);

        if (combatEvent == null) {
            return;
        }

        Optional<CustomEnchantment> customEnchantmentOptional = CustomEnchantmentWrapper
                .getCustomEnchantment(combatEvent.item(), this.getCustomEnchantmentType());

        if (customEnchantmentOptional.isEmpty()) {
            return;
        }

        CustomEnchantment customEnchantment = customEnchantmentOptional.get();
        int DEFAULT_DURATION = 3;
        double INCREMENT_DURATION = 0.2;
        int enchantmentLevel = customEnchantment.getLevel();
        int duration = isAmplified
                ? (int) (DEFAULT_DURATION + (enchantmentLevel * INCREMENT_DURATION))
                : DEFAULT_DURATION * customEnchantment.getLevel();
        int amplifier = isAmplified ? enchantmentLevel : 0;

        PotionEffect potionEffect = new PotionEffect(
                potionEffectType,
                duration * (int) Common.TICK_PER_SECOND.getValue(),
                Math.min(amplifier, this.AMPLIFIER_LIMIT) // Limit maximum potion effect amplifier.
        );

        combatEvent.defendingEntity().addPotionEffect(potionEffect);
    }

    /**
     * Update potion effects based on item.
     *
     * @param potionEffects    potion effects
     * @param item             item
     * @param potionEffectType potion effect type as in {@link PotionEffectType#values()}
     */
    private void updatePotionEffectsBasedOnItem(
            HashMap<PotionEffectType, PotionEffect> potionEffects, ItemStack item, PotionEffectType potionEffectType
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
    public void updateCustomEffectsOnArmorChange(PlayerArmorChangeEvent event, PotionEffectType potionEffectType) {
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

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
    }

    @EventHandler
    public void onDamaged(EntityDamageEvent event) {
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
    }

    @EventHandler
    public void onPlayerArmorChange(PlayerArmorChangeEvent event) {
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
    }

}
