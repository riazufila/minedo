package net.minedo.mc.functionalities.skills;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
        final int MAX_SKILL_POINTS = 5;
        UUID playerUuid = player.getUniqueId();
        int skillPoint = this.skillPoints.get(playerUuid);

        if (skillPoint < MAX_SKILL_POINTS) {
            String SKILL_POINT_ICON = "\u25A0";
            int updatedSkillPoint = skillPoint + 1;
            Component component = Component
                    .text(SKILL_POINT_ICON.repeat(updatedSkillPoint))
                    .color(NamedTextColor.GRAY);

            skillPoints.put(playerUuid, updatedSkillPoint);
            player.sendActionBar(component);
        }
    }

}
