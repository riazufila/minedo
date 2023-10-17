package net.minedo.mc.functionalities.customenchantment.list;

import net.minedo.mc.constants.common.Common;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.constants.feedbacksound.FeedbackSound;
import net.minedo.mc.customevents.PlayerNonBlockInteractEvent;
import net.minedo.mc.functionalities.customenchantment.CombatEvent;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentHandler;
import net.minedo.mc.functionalities.utils.ShapeUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Ice enchantment.
 */
public class IceEnchantmentHandler extends CustomEnchantmentHandler implements Listener {

    private final HashMap<UUID, Integer> playerSkillPoints;

    /**
     * Initialize ice enchantment handler.
     */
    public IceEnchantmentHandler(@NotNull HashMap<UUID, Integer> playerSkillPoints) {
        super(CustomEnchantmentType.ICE);
        this.playerSkillPoints = playerSkillPoints;
    }

    @EventHandler
    public void onHit(@NotNull EntityDamageByEntityEvent event) {
        Entity defender = event.getEntity();
        Entity attacker = event.getDamager();
        CombatEvent combatEvent = super.isAbleToInflictCustomEnchantmentOnHit(defender, attacker);

        if (combatEvent == null || combatEvent.getCustomEnchantment() == null) {
            return;
        }

        final int DEFAULT_DURATION = 3;
        LivingEntity defendingEntity = combatEvent.getDefendingEntity();
        int enchantmentLevel = combatEvent.getCustomEnchantment().getLevel();
        int duration = DEFAULT_DURATION * enchantmentLevel;
        int freezeTicks = defendingEntity.getFreezeTicks();

        int updatedFreezeTicks = freezeTicks
                + (duration * (int) Common.TICK_PER_SECOND.getValue());

        defendingEntity.setFreezeTicks(updatedFreezeTicks);
    }

    @EventHandler
    public void onInteract(@NotNull PlayerNonBlockInteractEvent event) {
        Player player = event.getPlayer();
        EquipmentSlot equipmentSlot = event.getHand();
        ItemStack itemUsed = event.getItem();

        boolean isAbleToSkill = super.isPlayerAbleToSkill(player, equipmentSlot, itemUsed, this.playerSkillPoints);
        if (!isAbleToSkill) {
            return;
        }

        final double RADIUS = 5;
        FeedbackSound feedbackSound = FeedbackSound.ICE_SKILL;
        World world = player.getWorld();
        Location playerLocation = player.getLocation();

        for (Location spherePoint : ShapeUtils.getSphere(playerLocation, RADIUS)) {
            Random random = new Random();
            List<Material> potentialBlocks = new ArrayList<>() {{
                add(Material.ICE);
                add(Material.PACKED_ICE);
            }};

            Material selectedBlock = potentialBlocks.get(random.nextInt(potentialBlocks.size()));
            world.getBlockAt(spherePoint).setType(selectedBlock);
        }

        world.playSound(playerLocation, feedbackSound.getSound(),
                feedbackSound.getVolume(), feedbackSound.getPitch());
    }

}
