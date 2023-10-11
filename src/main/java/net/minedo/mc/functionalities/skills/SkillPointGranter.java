package net.minedo.mc.functionalities.skills;

import net.minedo.mc.constants.skillvalue.SkillValue;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

/**
 * Runnable to replenish skill points for player.
 */
public class SkillPointGranter extends BukkitRunnable {

    private final Player player;
    private final HashMap<UUID, Integer> skillPoints;

    /**
     * Initialize skill point granter.
     *
     * @param player      player
     * @param skillPoints skill points
     */
    public SkillPointGranter(@NotNull Player player, @NotNull HashMap<UUID, Integer> skillPoints) {
        this.player = player;
        this.skillPoints = skillPoints;
    }

    @Override
    public void run() {
        final int maxSkillPoints = SkillValue.MAX_SKILL_POINTS.getValue();
        UUID playerUuid = player.getUniqueId();
        Integer skillPoint = this.skillPoints.get(playerUuid);
        skillPoint = skillPoint == null ? 0 : skillPoint;

        if (skillPoint < maxSkillPoints) {
            int updatedSkillPoint = skillPoint + 1;
            skillPoints.put(playerUuid, updatedSkillPoint);
            SkillUtils.displaySkillPoints(player, updatedSkillPoint, maxSkillPoints, false);
        }
    }

}
