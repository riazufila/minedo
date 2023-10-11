package net.minedo.mc.functionalities.skills;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minedo.mc.constants.feedbacksound.FeedbackSound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
            player.getWorld().playSound(player, FeedbackSound.SKILL_NO_POINTS.getSound(), 0.7f, 2.0f);
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

}
