package net.minedo.mc.functionalities.customenchantment.list;

import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.constants.feedbacksound.FeedbackSound;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantment;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentHandler;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentWrapper;
import net.minedo.mc.functionalities.skills.SkillUtils;
import net.minedo.mc.functionalities.utils.ShapeUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Ice enchantment.
 */
public class IceEnchantmentHandler extends CustomEnchantmentHandler {

    private final HashMap<UUID, Integer> playerSkillPoints;

    /**
     * Initialize ice enchantment handler.
     */
    public IceEnchantmentHandler(@NotNull HashMap<UUID, Integer> playerSkillPoints) {
        super(CustomEnchantmentType.ICE);
        this.playerSkillPoints = playerSkillPoints;
    }

    @Override
    @EventHandler
    public void onInteract(@NotNull PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = super.isInteractValid(event);

        if (item == null) {
            return;
        }

        Optional<CustomEnchantment> customEnchantmentOptional = CustomEnchantmentWrapper
                .getCustomEnchantment(item, this.getCustomEnchantmentType());

        if (customEnchantmentOptional.isEmpty()) {
            return;
        }

        if (!SkillUtils.canSkill(player, this.playerSkillPoints)) {
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
