package net.minedo.mc.functionalities.itembuilder;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minedo.mc.Minedo;
import net.minedo.mc.constants.betteritemtype.BetterItemType;
import net.minedo.mc.models.betteritem.BetterItem;
import net.minedo.mc.models.betteritemattribute.BetterItemAttribute;
import net.minedo.mc.models.betteritemenchantment.BetterItemEnchantment;
import net.minedo.mc.models.betteritemlore.BetterItemLore;
import net.minedo.mc.repositories.betteritemrepository.BetterItemRepository;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.sampling.DiscreteProbabilityCollectionSampler;
import org.apache.commons.rng.simple.RandomSource;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

public class ItemBuilder implements Listener {

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final Minedo pluginInstance;

    public ItemBuilder(Minedo pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    @EventHandler
    public void onChunkPopulate(ChunkPopulateEvent event) {
        BlockState[] tileEntities = event.getChunk().getTileEntities();

        for (BlockState state : tileEntities) {
            if (state.getBlock().getState() instanceof Chest chest) {
                ItemStack item = this.getBetterItem();

                if (item != null) {
                    chest.getInventory().addItem(item);
                }
            }
        }
    }

    private List<String> sliceString(String input) {
        List<String> result = new ArrayList<>();

        // Split the input string by whitespace.
        String[] words = input.split("\\s+");

        // Iterate through each word.
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            if (builder.length() + word.length() <= 40) {
                // Append the word to the current line.
                builder.append(word).append(" ");
            } else {
                // Add the current line to the result list.
                result.add(builder.toString().trim());
                // Start a new line with the current word.
                builder = new StringBuilder(word).append(" ");
            }
        }

        // Add the last line to the result list.
        result.add(builder.toString().trim());

        return result;
    }

    public ItemStack buildItem(BetterItem betterItem) {
        ItemStack item = new ItemStack(betterItem.getMaterial());
        ItemMeta meta = item.getItemMeta();

        // Set NBT tag.
        NamespacedKey typeKey = new NamespacedKey(this.pluginInstance, "type");
        meta.getPersistentDataContainer()
                .set(typeKey, PersistentDataType.STRING, BetterItemType.CUSTOM.getType());

        // Set display name.
        Component displayNameComponent = Component
                .text(betterItem.getDisplayName())
                .decoration(TextDecoration.ITALIC, false);

        if (betterItem.getColor() != null) {
            displayNameComponent = displayNameComponent.color(TextColor.fromHexString(betterItem.getColor()));
        }

        if (betterItem.getDecoration() != null) {
            displayNameComponent = displayNameComponent.decorate(betterItem.getDecoration());
        }

        meta.displayName(displayNameComponent);

        // Set lore.
        BetterItemLore betterItemLore = betterItem.getLore();
        List<Component> list = new ArrayList<>();

        for (String slicedLoreText : this.sliceString(betterItemLore.getText())) {
            Component loreComponent = Component.text(slicedLoreText).decoration(TextDecoration.ITALIC, false);

            if (betterItemLore.getColor() != null) {
                loreComponent = loreComponent.color(TextColor.fromHexString(betterItemLore.getColor()));
            }

            if (betterItemLore.getDecoration() != null) {
                loreComponent = loreComponent.decorate(betterItemLore.getDecoration());
            }

            list.add(loreComponent);
        }

        meta.lore(list);

        // Add enchantments.
        if (betterItem.getEnchantments() != null) {
            for (BetterItemEnchantment enchantment : betterItem.getEnchantments()) {
                meta.addEnchant(enchantment.getEnchantment(), enchantment.getLevel(), true);
            }
        }

        // Add attributes.
        if (betterItem.getAttributes() != null) {
            for (BetterItemAttribute attribute : betterItem.getAttributes()) {
                meta.addAttributeModifier(attribute.getAttribute(), new AttributeModifier(
                        UUID.randomUUID(), attribute.getAttribute().toString(), attribute.getModifier(),
                        attribute.getOperation(), attribute.getSlot()
                ));
            }
        }

        // Set item with new metas.
        item.setItemMeta(meta);

        return item;
    }

    public ItemStack getBetterItem() {
        try {
            Random random = new Random();

            // 50% chance to retrieve an item.
            if (random.nextBoolean()) {
                BetterItemRepository betterItemRepository = new BetterItemRepository();
                List<BetterItem> betterItemList = betterItemRepository.getAllBetterItems();

                double[] probabilities = new double[betterItemList.size()];
                int index = 0;

                for (BetterItem betterItem : betterItemList) {
                    probabilities[index] = betterItem.getProbability().getProbability();
                    index++;
                }

                // Randomly select one Better Item.
                UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_128_PP.create();
                BetterItem selectedBetterItem = new DiscreteProbabilityCollectionSampler<>(
                        rng, betterItemList, probabilities
                ).sample();

                return buildItem(selectedBetterItem);
            }
        } catch (Exception exception) {
            this.logger.severe(String.format("Unable to prepare better items: %s", exception.getMessage()));
        }

        return null;
    }

}
