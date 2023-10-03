package net.minedo.mc.functionalities.itembuilder;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minedo.mc.Minedo;
import net.minedo.mc.constants.customitemtype.CustomItemType;
import net.minedo.mc.models.customitem.CustomItem;
import net.minedo.mc.models.customitemattribute.CustomItemAttribute;
import net.minedo.mc.models.customitemenchantment.CustomItemEnchantment;
import net.minedo.mc.models.customitemlore.CustomItemLore;
import net.minedo.mc.repositories.customitemrepository.CustomItemRepository;
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
                ItemStack item = this.getCustomItem();

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

    public ItemStack buildItem(CustomItem customItem) {
        ItemStack item = new ItemStack(customItem.getMaterial());
        ItemMeta meta = item.getItemMeta();

        // Set NBT tag.
        NamespacedKey typeKey = new NamespacedKey(this.pluginInstance, "type");
        meta.getPersistentDataContainer()
                .set(typeKey, PersistentDataType.STRING, CustomItemType.CUSTOM_ITEM.getType());

        // Set display name.
        Component displayNameComponent = Component
                .text(customItem.getDisplayName())
                .decoration(TextDecoration.ITALIC, false);

        if (customItem.getColor() != null) {
            displayNameComponent = displayNameComponent.color(TextColor.fromHexString(customItem.getColor()));
        }

        if (customItem.getDecoration() != null) {
            displayNameComponent = displayNameComponent.decorate(customItem.getDecoration());
        }

        meta.displayName(displayNameComponent);

        // Set lore.
        CustomItemLore customItemLore = customItem.getLore();
        List<Component> list = new ArrayList<>();

        for (String slicedLoreText : this.sliceString(customItemLore.getText())) {
            Component loreComponent = Component.text(slicedLoreText).decoration(TextDecoration.ITALIC, false);

            if (customItemLore.getColor() != null) {
                loreComponent = loreComponent.color(TextColor.fromHexString(customItemLore.getColor()));
            }

            if (customItemLore.getDecoration() != null) {
                loreComponent = loreComponent.decorate(customItemLore.getDecoration());
            }

            list.add(loreComponent);
        }

        meta.lore(list);

        // Add enchantments.
        if (customItem.getEnchantments() != null) {
            for (CustomItemEnchantment enchantment : customItem.getEnchantments()) {
                meta.addEnchant(enchantment.getEnchantment(), enchantment.getLevel(), true);
            }
        }

        // Add attributes.
        if (customItem.getAttributes() != null) {
            for (CustomItemAttribute attribute : customItem.getAttributes()) {
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

    public ItemStack getCustomItem() {
        try {
            Random random = new Random();

            // 50% chance to retrieve an item.
            if (random.nextBoolean()) {
                CustomItemRepository customItemRepository = new CustomItemRepository();
                List<CustomItem> customItemList = customItemRepository.getAllCustomItems();

                double[] probabilities = new double[customItemList.size()];
                int index = 0;

                for (CustomItem customItem : customItemList) {
                    probabilities[index] = customItem.getProbability().getProbability();
                    index++;
                }

                // Randomly select one custom item.
                UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_128_PP.create();
                CustomItem selectedCustomItem = new DiscreteProbabilityCollectionSampler<>(
                        rng, customItemList, probabilities
                ).sample();

                return buildItem(selectedCustomItem);
            }
        } catch (Exception exception) {
            this.logger.severe(String.format("Unable to prepare custom items: %s", exception.getMessage()));
        }

        return null;
    }

}
