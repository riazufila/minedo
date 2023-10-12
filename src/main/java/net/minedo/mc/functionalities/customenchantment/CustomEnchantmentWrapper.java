package net.minedo.mc.functionalities.customenchantment;

import net.minedo.mc.Minedo;
import net.minedo.mc.constants.common.Common;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.list.*;
import net.minedo.mc.functionalities.dataembedder.DataEmbedder;
import net.minedo.mc.functionalities.skills.SkillPointGranter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Custom enchantment wrapper. Handles custom enchantment handler initializer.
 */
public class CustomEnchantmentWrapper implements Listener {

    private final HashMap<UUID, Integer> skillPointGranters = new HashMap<>();
    private final HashMap<UUID, Integer> playerSkillPoints = new HashMap<>();

    /**
     * Get custom enchantment.
     *
     * @param item                  item
     * @param customEnchantmentType custom enchantment type as in {@link CustomEnchantmentType#values()}
     * @return custom enchantment
     */
    public static @NotNull Optional<CustomEnchantment> getCustomEnchantment(
            @NotNull ItemStack item, @NotNull CustomEnchantmentType customEnchantmentType
    ) {
        List<CustomEnchantment> customEnchantments = DataEmbedder.getCustomEnchantments(item);

        if (customEnchantments != null && !customEnchantments.isEmpty()) {
            return customEnchantments
                    .stream()
                    .filter(enchantment -> enchantment
                            .getCustomEnchantmentType()
                            .equals(customEnchantmentType))
                    .findFirst();
        }

        return Optional.empty();
    }

    /**
     * Format custom enchantment name.
     *
     * @param enchantmentName enchantment name based on {@link CustomEnchantmentType#values()}
     * @return formatted custom enchantment name
     */
    public static @NotNull String formatCustomEnchantmentName(@NotNull String enchantmentName) {
        String[] words = enchantmentName.split("_");

        StringBuilder formattedName = new StringBuilder();

        // Iterate through the words and capitalize the first letter of each word
        for (String word : words) {
            // Append a space if the formattedName is not empty
            if (!formattedName.isEmpty()) {
                formattedName.append(" ");
            }

            // Append the word with the first letter capitalized
            formattedName.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1).toLowerCase());
        }

        return formattedName.toString();
    }

    /**
     * Register the listeners for all custom enchantments.
     */
    public void registerCustomEnchantments() {
        Minedo instance = Minedo.getInstance();
        List<CustomEnchantmentHandler> customEnchantmentHandlers = new ArrayList<>();

        customEnchantmentHandlers.add(new AbsorptionEnchantmentHandler());
        customEnchantmentHandlers.add(new BlindnessEnchantmentHandler());
        customEnchantmentHandlers.add(new ConfusionEnchantmentHandler());
        customEnchantmentHandlers.add(new DarknessEnchantmentHandler());
        customEnchantmentHandlers.add(new ExplosionEnchantmentHandler(this.playerSkillPoints));
        customEnchantmentHandlers.add(new FireResistanceEnchantmentHandler());
        customEnchantmentHandlers.add(new GlowingEnchantmentHandler());
        customEnchantmentHandlers.add(new HarmEnchantmentHandler());
        customEnchantmentHandlers.add(new HasteEnchantmentHandler());
        customEnchantmentHandlers.add(new HealEnchantmentHandler());
        customEnchantmentHandlers.add(new HealthBoostEnchantmentHandler());
        customEnchantmentHandlers.add(new HungerEnchantmentHandler());
        customEnchantmentHandlers.add(new IceEnchantmentHandler(this.playerSkillPoints));
        customEnchantmentHandlers.add(new InvisibilityEnchantmentHandler());
        customEnchantmentHandlers.add(new JumpEnchantmentHandler());
        customEnchantmentHandlers.add(new LightningEnchantmentHandler(this.playerSkillPoints));
        customEnchantmentHandlers.add(new PoisonEnchantmentHandler());
        customEnchantmentHandlers.add(new RegenerationEnchantmentHandler());
        customEnchantmentHandlers.add(new ResistanceEnchantmentHandler());
        customEnchantmentHandlers.add(new SlowEnchantmentHandler());
        customEnchantmentHandlers.add(new SpeedEnchantmentHandler());
        customEnchantmentHandlers.add(new StrengthEnchantmentHandler());
        customEnchantmentHandlers.add(new WaterBreathingEnchantmentHandler());
        customEnchantmentHandlers.add(new WeaknessEnchantmentHandler());
        customEnchantmentHandlers.add(new WitherEnchantmentHandler());

        for (CustomEnchantmentHandler customEnchantmentHandler : customEnchantmentHandlers) {
            instance.getServer().getPluginManager().registerEvents(customEnchantmentHandler, instance);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUuid = player.getUniqueId();

        // Keep track of player skill points.
        final long DURATION = 10;
        Integer skillPoint = playerSkillPoints.get(playerUuid);
        playerSkillPoints.put(playerUuid, skillPoint != null ? skillPoint : 0);
        int skillPointGranterTaskId = new SkillPointGranter(player, playerSkillPoints)
                .runTaskTimer(
                        Minedo.getInstance(),
                        DURATION * (int) Common.TICK_PER_SECOND.getValue(),
                        DURATION * (int) Common.TICK_PER_SECOND.getValue())
                .getTaskId();

        // Keep track of the runnable executed per player.
        skillPointGranters.put(playerUuid, skillPointGranterTaskId);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUuid = player.getUniqueId();

        // Cancel runnable and remove from HashMap when player quits.
        Integer skillPointGranterTaskId = skillPointGranters.get(playerUuid);
        if (skillPointGranterTaskId != null) {
            Bukkit.getScheduler().cancelTask(skillPointGranterTaskId);
            skillPointGranters.remove(playerUuid);
        }
    }

}
