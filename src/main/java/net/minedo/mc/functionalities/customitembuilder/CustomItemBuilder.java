package net.minedo.mc.functionalities.customitembuilder;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantment;
import net.minedo.mc.functionalities.dataembedder.DataEmbedder;
import net.minedo.mc.models.customitem.CustomItem;
import net.minedo.mc.models.customitemenchantment.CustomItemEnchantment;
import net.minedo.mc.models.customitemlore.CustomItemLore;
import net.minedo.mc.models.customitemprobability.CustomItemProbability;
import net.minedo.mc.repositories.customitemrepository.CustomItemRepository;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.sampling.DiscreteProbabilityCollectionSampler;
import org.apache.commons.rng.simple.RandomSource;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class CustomItemBuilder implements Listener {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @EventHandler
    public void onChunkPopulate(ChunkPopulateEvent event) {
        BlockState[] tileEntities = event.getChunk().getTileEntities();

        for (BlockState state : tileEntities) {
            BlockState blockState = state.getBlock().getState();
            if (blockState instanceof Container container) {
                ItemStack item = this.getCustomItem();

                if (item != null) {
                    container.getInventory().addItem(item);
                }
            }
        }
    }

    private void applyVanillaEnchantments(ItemMeta meta, List<EnchantmentContainer> enchantmentContainers) {
        for (EnchantmentContainer enchantmentContainer : enchantmentContainers) {
            meta.addEnchant(enchantmentContainer.enchantment(), enchantmentContainer.level(), true);
        }
    }

    private ItemStack buildItem(CustomItem customItem) {
        ItemStack item = new ItemStack(customItem.getMaterial());
        ItemMeta meta = item.getItemMeta();

        // Set display name.
        Component displayNameComponent = Component
                .text(customItem.getDisplayName())
                .decoration(TextDecoration.ITALIC, false);

        if (customItem.getColor() != null) {
            displayNameComponent = displayNameComponent.color(customItem.getColor());
        }

        if (customItem.getDecoration() != null) {
            displayNameComponent = displayNameComponent.decorate(customItem.getDecoration());
        }

        meta.displayName(displayNameComponent);

        // Classify enchantments.
        List<CustomEnchantment> customEnchantments = new ArrayList<>();
        List<EnchantmentContainer> vanillaEnchantments = new ArrayList<>();
        List<CustomItemEnchantment> customItemEnchantments = customItem.getEnchantments();
        if (!customItemEnchantments.isEmpty()) {
            for (CustomItemEnchantment enchantment : customItem.getEnchantments()) {
                String unknownEnchantment = enchantment.getEnchantment();

                Enchantment vanillaEnchantment = Enchantment
                        .getByKey(NamespacedKey.minecraft(unknownEnchantment.toLowerCase()));
                if (vanillaEnchantment != null) {
                    vanillaEnchantments.add(new EnchantmentContainer(vanillaEnchantment, enchantment.getLevel()));
                } else {
                    CustomEnchantmentType customEnchantment = CustomEnchantmentType.valueOf(unknownEnchantment);
                    customEnchantments.add(new CustomEnchantment(customEnchantment, (short) enchantment.getLevel()));
                }
            }

            // Add all enchantments.
            this.applyVanillaEnchantments(meta, vanillaEnchantments);
            DataEmbedder.applyCustomEnchantments(meta, customEnchantments);
        }

        // Set lore.
        CustomItemLore customItemLore = customItem.getLore();
        List<Component> list = LoreUtils.getLoreComponents(customItemLore.getText(),
                customItemLore.getColor(), customItemLore.getDecoration());
        LoreUtils.updateLore(meta, list, true);


        // Set item with new metas.
        item.setItemMeta(meta);

        return item;
    }

    public ItemStack getCustomItem() {
        try {
            Random random = new Random();

            // 50% chance to retrieve an item.
            if (random.nextBoolean()) {
                List<CustomItemProbability> customItemsProbabilities = CustomItemRepository
                        .getAllCustomItemsProbabilities();

                double[] probabilities = new double[customItemsProbabilities.size()];
                int index = 0;

                for (CustomItemProbability customItemProbability : customItemsProbabilities) {
                    probabilities[index] = customItemProbability.getProbability();
                    index++;
                }

                // Randomly select one custom item.
                UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_128_PP.create();
                CustomItemProbability sample = new DiscreteProbabilityCollectionSampler<>(
                        rng, customItemsProbabilities, probabilities
                ).sample();

                CustomItem customItem = CustomItemRepository.getCustomItemById(sample.getCustomItemId());
                return this.buildItem(customItem);
            }
        } catch (Exception exception) {
            this.logger.severe(String.format("Unable to prepare custom items: %s", exception.getMessage()));
        }

        return null;
    }

}
