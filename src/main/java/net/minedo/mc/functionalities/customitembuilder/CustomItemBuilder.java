package net.minedo.mc.functionalities.customitembuilder;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantment;
import net.minedo.mc.functionalities.dataembedder.DataEmbedder;
import net.minedo.mc.models.customitem.CustomItem;
import net.minedo.mc.models.customitemenchantment.CustomItemEnchantment;
import net.minedo.mc.models.customitemlore.CustomItemLore;
import net.minedo.mc.models.customitemprobability.CustomItemProbability;
import net.minedo.mc.repositories.customitemprobabilityrepository.CustomItemProbabilityRepository;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Custom item builder.
 */
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
                    Inventory inventory = container.getInventory();
                    Random random = new Random();
                    int index = random.nextInt(inventory.getSize());

                    inventory.setItem(index, item);
                }
            }
        }
    }

    /**
     * Apply vanilla enchantments.
     *
     * @param meta                  item meta
     * @param enchantmentContainers enchantments
     */
    private void applyVanillaEnchantments(
            @NotNull ItemMeta meta, @NotNull List<EnchantmentContainer> enchantmentContainers
    ) {
        for (EnchantmentContainer enchantmentContainer : enchantmentContainers) {
            meta.addEnchant(enchantmentContainer.enchantment(), enchantmentContainer.level(), true);
        }
    }

    /**
     * Build display name.
     *
     * @param meta       item meta
     * @param customItem custom item
     */
    private void buildDisplayName(@NotNull ItemMeta meta, @NotNull CustomItem customItem) {
        Component displayNameComponent = Component
                .text(customItem.displayName())
                .decoration(TextDecoration.ITALIC, false);

        if (customItem.color() != null) {
            displayNameComponent = displayNameComponent.color(customItem.color());
        }

        if (customItem.decoration() != null) {
            displayNameComponent = displayNameComponent.decorate(customItem.decoration());
        }

        displayNameComponent = displayNameComponent.colorIfAbsent(NamedTextColor.WHITE);
        meta.displayName(displayNameComponent);
    }

    /**
     * Build unknown enchantments.
     *
     * @param meta       item meta
     * @param customItem custom item
     */
    private void buildEnchantments(@NotNull ItemMeta meta, @NotNull CustomItem customItem) {
        List<CustomItemEnchantment> customItemEnchantments = customItem.enchantments();

        if (customItemEnchantments == null) {
            return;
        }

        if (customItemEnchantments.isEmpty()) {
            return;
        }

        // Classify enchantments.
        List<CustomEnchantment> customEnchantments = new ArrayList<>();
        List<EnchantmentContainer> vanillaEnchantments = new ArrayList<>();

        for (CustomItemEnchantment enchantment : customItemEnchantments) {
            String unknownEnchantment = enchantment.enchantment();

            if (!enchantment.isCustom()) {
                NamespacedKey vanillaEnchantmentKey = NamespacedKey.minecraft(unknownEnchantment.toLowerCase());
                Enchantment vanillaEnchantment = Enchantment.getByKey(vanillaEnchantmentKey);

                if (vanillaEnchantment == null) {
                    throw new IllegalStateException("Vanilla enchantment cannot be null.");
                }

                vanillaEnchantments.add(new EnchantmentContainer(vanillaEnchantment, enchantment.level()));
            } else {
                CustomEnchantmentType customEnchantment = CustomEnchantmentType.valueOf(unknownEnchantment);
                customEnchantments.add(new CustomEnchantment(customEnchantment, (short) enchantment.level()));
            }
        }

        // Add all enchantments.
        this.applyVanillaEnchantments(meta, vanillaEnchantments);
        DataEmbedder.applyCustomEnchantments(meta, customEnchantments);
    }

    /**
     * Build item lore.
     *
     * @param meta       item meta
     * @param customItem custom item
     */
    private void buildLore(@NotNull ItemMeta meta, @NotNull CustomItem customItem) {
        CustomItemLore customItemLore = customItem.lore();

        if (customItemLore == null) {
            return;
        }

        List<Component> list = LoreUtils.getLoreComponents(customItemLore.text(),
                customItemLore.color(), customItemLore.decoration());
        LoreUtils.updateLore(meta, list, true);
    }

    /**
     * Build item.
     *
     * @param customItem custom item
     * @return item
     */
    private @NotNull ItemStack buildItem(@NotNull CustomItem customItem) {
        ItemStack item = new ItemStack(customItem.material());
        ItemMeta meta = item.getItemMeta();

        this.buildDisplayName(meta, customItem);
        this.buildEnchantments(meta, customItem);
        this.buildLore(meta, customItem);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * Get custom item.
     *
     * @return custom item
     */
    public ItemStack getCustomItem() {
        try {
            Random random = new Random();
            double PROBABILITY_NEEDED = 0.3;

            if (PROBABILITY_NEEDED >= random.nextDouble()) {
                List<CustomItemProbability> customItemsProbabilities = CustomItemProbabilityRepository
                        .getAllCustomItemsProbabilities();

                double[] probabilities = new double[customItemsProbabilities.size()];
                int index = 0;

                for (CustomItemProbability customItemProbability : customItemsProbabilities) {
                    probabilities[index] = customItemProbability.probability();
                    index++;
                }

                // Randomly select one custom item.
                UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_128_PP.create();
                CustomItemProbability sample = new DiscreteProbabilityCollectionSampler<>(
                        rng, customItemsProbabilities, probabilities
                ).sample();

                CustomItem customItem = CustomItemRepository.getCustomItemById(sample.customItemId());

                if (customItem == null) {
                    return null;
                }

                return this.buildItem(customItem);
            }
        } catch (Exception exception) {
            this.logger.severe(String.format("Unable to prepare custom items: %s", exception.getMessage()));
        }

        return null;
    }

}
