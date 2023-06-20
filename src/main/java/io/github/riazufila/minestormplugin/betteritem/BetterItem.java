package io.github.riazufila.minestormplugin.betteritem;

import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.inventory.ItemStack;

public class BetterItem implements Listener {

    @EventHandler
    public void onChunkPopulate(ChunkPopulateEvent event) {
        BlockState[] tileEntities = event.getChunk().getTileEntities();

        for (BlockState state : tileEntities) {
            if (state.getBlock().getState() instanceof Chest chest) {
                ItemStack item = new ItemBuilder().prepareItem();

                if (item != null) {
                    chest.getInventory().addItem(item);
                }
            }
        }
    }

}
