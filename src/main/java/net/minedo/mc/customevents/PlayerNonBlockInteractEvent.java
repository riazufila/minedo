package net.minedo.mc.customevents;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an event that is called by {@link org.bukkit.event.player.PlayerInteractEvent},
 * but when an item isn't placed, block state don't change, and when no block is interacted with.
 */
public class PlayerNonBlockInteractEvent extends PlayerInteractEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public PlayerNonBlockInteractEvent(@NotNull Player who, @NotNull Action action, @Nullable ItemStack item, @Nullable Block clickedBlock, @NotNull BlockFace clickedFace, @Nullable EquipmentSlot hand) {
        super(who, action, item, clickedBlock, clickedFace, hand);
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

}
