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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public abstract class CustomEnchantmentHandler extends SimpleCustomEnchantment implements Listener {

    public CustomEnchantmentHandler(CustomEnchantmentType customEnchantmentType) {
        super(customEnchantmentType);
    }

    public CombatEvent isOnHitValid(EntityDamageByEntityEvent event) {
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
        int duration = isAmplified ? 3 : customEnchantment.getLevel();
        int amplifier = isAmplified ? customEnchantment.getLevel() : 0;
        PotionEffect potionEffect = new PotionEffect(
                potionEffectType,
                duration * (int) Common.TICK_PER_SECOND.getValue(),
                amplifier
        );

        combatEvent.defendingEntity().addPotionEffect(potionEffect);
    }

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
                amplifier
        );

        potionEffects.put(potionEffectType, potionEffect);
    }

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
        // Override and add custom effects if needed.
    }

    @EventHandler
    public void onDamaged(EntityDamageEvent event) {
        // Override and add custom effects if needed.
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        // Override and add custom effects if needed.
    }

    @EventHandler
    public void onPlayerArmorChange(PlayerArmorChangeEvent event) {
        // Override and add custom effects if needed.
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        // Override and add custom effects if needed.
    }

}
