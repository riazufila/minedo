package net.minedo.mc.functionalities.common.utils;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Player utils.
 */
public final class PlayerUtils {

    /**
     * Get whether player is moving.
     *
     * @param event event
     * @return whether player is moving
     */
    public static boolean isPlayerMoving(PlayerMoveEvent event) {
        return event.getFrom().getBlockX() != event.getTo().getBlockX() ||
                event.getFrom().getBlockY() != event.getTo().getBlockY() ||
                event.getFrom().getBlockZ() != event.getTo().getBlockZ();
    }

    /**
     * Get whether player can fall through block.
     *
     * @param block block
     * @return whether player can fall through block
     */
    private static boolean canPlayerFallThroughBlock(Block block) {
        return block.isPassable()
                || block.isLiquid()
                || block.isEmpty();
    }

    /**
     * Get whether player is falling,
     *
     * @param event event
     * @return whether player is falling
     */
    public static boolean isPlayerFalling(PlayerMoveEvent event) {
        Block block = event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN);

        return canPlayerFallThroughBlock(block);
    }

}
