package net.minedo.mc.functionalities.skills;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minedo.mc.constants.feedbacksound.FeedbackSound;
import net.minedo.mc.constants.skillvalue.SkillValue;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

/**
 * Skill utils.
 */
public final class SkillUtils {

    private final static String skillPointCoolDown = "\u2581";
    private final static String skillPointIcon = "\u2588";

    /**
     * Display skill points.
     *
     * @param player         player
     * @param skillPoint     skill point
     * @param maxSkillPoints max skill points
     * @param depleted       whether skill points are already depleted
     */
    public static void displaySkillPoints(
            @NotNull Player player, int skillPoint, int maxSkillPoints, boolean depleted
    ) {
        if (skillPoint == 0 && depleted) {
            FeedbackSound feedbackSound = FeedbackSound.SKILL_NO_POINTS;

            player.getWorld().playSound(player, feedbackSound.getSound(),
                    feedbackSound.getVolume(), feedbackSound.getPitch());
        }

        Component component = Component
                .text(skillPointIcon
                        .repeat(skillPoint)
                        .concat(skillPointCoolDown
                                .repeat(maxSkillPoints - skillPoint)
                        )
                )
                .color(NamedTextColor.GRAY);

        player.sendActionBar(component);
    }

    /**
     * Get whether player can use skill.
     *
     * @param player            player
     * @param playerSkillPoints player skill points
     * @return whether player can use skill
     */
    public static boolean canSkill(@NotNull Player player, @NotNull HashMap<UUID, Integer> playerSkillPoints) {
        UUID playerUuid = player.getUniqueId();
        int MAX_SKILL_POINTS = SkillValue.MAX_SKILL_POINTS.getValue();
        Integer skillPoint = playerSkillPoints.get(playerUuid);
        skillPoint = skillPoint == null ? 0 : skillPoint;

        if (skillPoint == 0) {
            SkillUtils.displaySkillPoints(player, 0, MAX_SKILL_POINTS, true);
            return false;
        }

        // Remove a skill point.
        int updatedSkillPoint = skillPoint - 1;
        playerSkillPoints.put(playerUuid, updatedSkillPoint);
        SkillUtils.displaySkillPoints(player, updatedSkillPoint, MAX_SKILL_POINTS, false);

        return true;
    }

}
