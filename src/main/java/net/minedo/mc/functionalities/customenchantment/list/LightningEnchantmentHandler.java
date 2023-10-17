package net.minedo.mc.functionalities.customenchantment.list;

import net.minedo.mc.Minedo;
import net.minedo.mc.constants.common.Common;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.constants.feedbacksound.FeedbackSound;
import net.minedo.mc.customevents.PlayerNonBlockInteractEvent;
import net.minedo.mc.functionalities.customenchantment.CombatEvent;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentHandler;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

/**
 * Spawn lightning upon hit.
 */
public class LightningEnchantmentHandler extends CustomEnchantmentHandler implements Listener {

    private final HashMap<UUID, Integer> playerSkillPoints;

    /**
     * Initialize lighting enchantment handler.
     */
    public LightningEnchantmentHandler(@NotNull HashMap<UUID, Integer> playerSkillPoints) {
        super(CustomEnchantmentType.LIGHTNING);
        this.playerSkillPoints = playerSkillPoints;
    }

    /**
     * Run a scheduler to strike lightnings.
     *
     * @param player player
     * @param delay  delay
     */
    private void straightLightningRunnable(@NotNull Player player, long delay) {
        World world = player.getWorld();
        Location startLocation = player.getLocation();
        Location originLocation = startLocation.clone();

        new BukkitRunnable() {

            final int FIRST_COUNT = 1;
            int countDown = FIRST_COUNT;

            @Override
            public void run() {
                if (countDown > delay) {
                    this.cancel();
                }

                Vector direction = startLocation.getDirection().clone().multiply(3);

                if (countDown == FIRST_COUNT) {
                    startLocation.add(direction);
                } else {
                    startLocation.add(direction).add(Vector.getRandom());
                }

                Location strikeLocation = startLocation.clone();

                if (countDown == FIRST_COUNT) {
                    if (strikeLocation.getBlockY() < world.getHighestBlockYAt(strikeLocation)) {
                        FeedbackSound feedbackSound = FeedbackSound.LIGHTNING_SKILL_FAIL;

                        world.playSound(originLocation, feedbackSound.getSound(),
                                feedbackSound.getVolume(), feedbackSound.getPitch());

                        this.cancel();
                        return;
                    }
                }

                strikeLocation.setY(world.getHighestBlockYAt(strikeLocation));
                world.strikeLightning(strikeLocation).setCausingPlayer(player);

                countDown++;
            }

        }.runTaskTimer(
                Minedo.getInstance(),
                0,
                (int) Common.TICK_PER_SECOND.getValue() / 4
        );
    }

    @EventHandler
    public void onHit(@NotNull EntityDamageByEntityEvent event) {
        CombatEvent combatEvent = super.isAbleToInflictCustomEnchantmentHits(event);

        if (combatEvent == null || combatEvent.getCustomEnchantment() == null) {
            return;
        }

        Location location = event.getEntity().getLocation();
        LivingEntity defendingEntity = combatEvent.getDefendingEntity();
        LivingEntity attackingEntity = combatEvent.getAttackingEntity();
        World world = defendingEntity.getWorld();

        if (location.getBlockY() < world.getHighestBlockYAt(location)) {
            return;
        }

        LightningStrike lightningStrike = defendingEntity
                .getWorld()
                .strikeLightning(defendingEntity.getLocation());

        if (attackingEntity instanceof Player attackingPlayer) {
            lightningStrike.setCausingPlayer(attackingPlayer);
        }
    }

    @EventHandler
    public void onInteract(@NotNull PlayerNonBlockInteractEvent event) {
        Player player = event.getPlayer();
        boolean isAbleToSkill = super.isPlayerAbleToSkill(event, player, this.playerSkillPoints);

        if (!isAbleToSkill) {
            return;
        }

        final long DELAY = 5;
        this.straightLightningRunnable(player, DELAY);
    }

}
