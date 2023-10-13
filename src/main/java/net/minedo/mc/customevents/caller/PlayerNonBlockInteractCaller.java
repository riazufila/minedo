package net.minedo.mc.customevents.caller;

import net.minedo.mc.Minedo;
import net.minedo.mc.constants.common.Common;
import net.minedo.mc.customevents.PlayerNonBlockInteractEvent;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Calls the event {@link PlayerNonBlockInteractEvent}.
 */
public class PlayerNonBlockInteractCaller implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();

        if (!action.isRightClick() || event.isBlockInHand()) {
            return;
        }

        final Block CLICKED_BLOCK = event.getClickedBlock();
        PlayerNonBlockInteractEvent blockUnchangedEvent = new PlayerNonBlockInteractEvent(
                event.getPlayer(),
                event.getAction(),
                event.getItem(),
                event.getClickedBlock(),
                event.getBlockFace(),
                event.getHand()
        );

        if (CLICKED_BLOCK != null) {
            BlockState previousBlockState = CLICKED_BLOCK.getState();

            new BukkitRunnable() {

                @Override
                public void run() {
                    BlockState currentBlockState = CLICKED_BLOCK.getState();

                    if (previousBlockState.equals(currentBlockState)) {
                        System.out.println("block state unchanged");
                        System.out.println("calling custom event");
                        blockUnchangedEvent.callEvent();
                    }
                }

            }.runTaskLater(Minedo.getInstance(), (int) Common.TICK_PER_SECOND.getValue() / 20);
        } else {
            System.out.println("no block clicked");
            System.out.println("calling custom event");
            blockUnchangedEvent.callEvent();
        }
    }

}
