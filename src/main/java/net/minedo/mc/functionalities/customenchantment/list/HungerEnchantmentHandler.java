package net.minedo.mc.functionalities.customenchantment.list;

import net.minedo.mc.constants.common.Common;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.CombatEvent;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantment;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentHandler;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentWrapper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class HungerEnchantmentHandler extends CustomEnchantmentHandler {

    public HungerEnchantmentHandler() {
        super(CustomEnchantmentType.HUNGER);
    }

    @Override
    @EventHandler
    public void onHit(@NotNull EntityDamageByEntityEvent event) {
        CombatEvent combatEvent = super.isOnHitValid(event);

        if (combatEvent == null) {
            return;
        }

        Optional<CustomEnchantment> customEnchantmentOptional = CustomEnchantmentWrapper
                .getCustomEnchantment(combatEvent.item(), this.getCustomEnchantmentType());

        if (customEnchantmentOptional.isEmpty()) {
            return;
        }

        CustomEnchantment customEnchantment = customEnchantmentOptional.get();
        int EFFECT_DURATION = 3;

        PotionEffect potionEffect = new PotionEffect(
                PotionEffectType.HUNGER,
                EFFECT_DURATION * (int) Common.TICK_PER_SECOND.getValue(),
                customEnchantment.getLevel() - 1
        );

        combatEvent.defendingEntity().addPotionEffect(potionEffect);
    }

}
