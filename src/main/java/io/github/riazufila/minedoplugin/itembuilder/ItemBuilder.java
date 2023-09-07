package io.github.riazufila.minedoplugin.itembuilder;

import io.github.riazufila.minedoplugin.MinedoPlugin;
import io.github.riazufila.minedoplugin.database.model.betteritem.BetterItem;
import io.github.riazufila.minedoplugin.database.model.betteritemattribute.BetterItemAttribute;
import io.github.riazufila.minedoplugin.database.model.betteritemenchantment.BetterItemEnchantment;
import io.github.riazufila.minedoplugin.database.model.betteritemlore.BetterItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
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

import java.time.Instant;
import java.util.*;

public class ItemBuilder implements Listener {

    private final MinedoPlugin pluginInstance;

    public ItemBuilder(MinedoPlugin pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    @EventHandler
    public void onChunkPopulate(ChunkPopulateEvent event) {
        BlockState[] tileEntities = event.getChunk().getTileEntities();

        for (BlockState state : tileEntities) {
            if (state.getBlock().getState() instanceof Chest chest) {
                ItemStack item = this.prepareItem();

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
        NamespacedKey subTypeKey = new NamespacedKey(this.pluginInstance, "subType");
        NamespacedKey uuidKey = new NamespacedKey(this.pluginInstance, "uuid");
        NamespacedKey timestampKey = new NamespacedKey(this.pluginInstance, "timestamp");
        meta.getPersistentDataContainer().set(typeKey, PersistentDataType.STRING, "ASTRAL_GEAR");
        meta.getPersistentDataContainer().set(subTypeKey, PersistentDataType.STRING, betterItem.getDisplayName());
        meta.getPersistentDataContainer().set(uuidKey, PersistentDataType.STRING, UUID.randomUUID().toString());
        meta.getPersistentDataContainer().set(timestampKey, PersistentDataType.STRING, Instant.now().toString());

        // Set display name.
        Component displayNameComponent = Component.text(betterItem.getDisplayName()).decoration(TextDecoration.ITALIC, false);

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
                meta.addEnchant(enchantment.enchantment, enchantment.level, true);
            }
        }

        // Add attributes.
        if (betterItem.getAttributes() != null) {
            for (BetterItemAttribute attribute : betterItem.getAttributes()) {
                meta.addAttributeModifier(attribute.attribute, new AttributeModifier(UUID.randomUUID(), attribute.attribute.toString(), attribute.modifier, attribute.operation, attribute.slot));
            }
        }

        // Set item with new metas.
        item.setItemMeta(meta);

        return item;
    }

    public ItemStack prepareItem() {
        Random random = new Random();

        // 50% chance to retrieve an item.
        if (random.nextBoolean()) {
            BetterItem[] betterItemList = BetterItem.getAllBetterItems();

            double[] probabilities = new double[betterItemList.length];
            int index = 0;

            for (BetterItem betterItem : betterItemList) {
                probabilities[index] = betterItem.getProbability().getProbability();
                index++;
            }

            // Randomly select one Better Item.
            UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_128_PP.create();
            BetterItem selectedBetterItem = new DiscreteProbabilityCollectionSampler<>(rng, Arrays.asList(betterItemList), probabilities).sample();

            return buildItem(selectedBetterItem);
        }

        return null;
    }

}
