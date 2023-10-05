package net.minedo.mc.functionalities.dataembedder;

import net.minedo.mc.Minedo;
import net.minedo.mc.constants.customenchantment.key.CustomEnchantmentKey;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantment;
import net.minedo.mc.functionalities.customenchantment.SimpleCustomEnchantment;
import net.minedo.mc.functionalities.customitembuilder.LoreUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * Uses PDC to store and get arbitrary data from within objects in Paper API.
 */
public final class DataEmbedder {

    /**
     * Create a key using PDC.
     *
     * @param key used as an identifier when setting and getting a value
     * @return key object
     */
    public static NamespacedKey createKey(String key) {
        return new NamespacedKey(Minedo.getInstance(), key);
    }

    /**
     * Apply custom enchantments into item.
     *
     * @param meta               item meta to apply data into
     * @param customEnchantments simplified custom enchantment list
     * @return item meta
     */
    public static ItemMeta applyCustomEnchantments(
            ItemMeta meta, List<CustomEnchantment> customEnchantments
    ) {
        PersistentDataContainer parentContainer = meta.getPersistentDataContainer();
        List<PersistentDataContainer> childContainers = new ArrayList<>();

        for (CustomEnchantment customEnchantment : customEnchantments) {
            PersistentDataContainer newContainer = parentContainer.getAdapterContext().newPersistentDataContainer();
            NamespacedKey idKey = createKey(CustomEnchantmentKey.CUSTOM_ENCHANTMENT_ID.getKey());
            NamespacedKey levelKey = createKey(CustomEnchantmentKey.CUSTOM_ENCHANTMENT_LEVEL.getKey());

            newContainer.set(idKey, PersistentDataType.STRING,
                    customEnchantment.getCustomEnchantmentType().toString().toLowerCase());
            newContainer.set(levelKey, PersistentDataType.SHORT, customEnchantment.getLevel());

            meta.lore(LoreUtils.getLoreComponents(customEnchantment.getLore()));

            childContainers.add(newContainer);
        }

        NamespacedKey customEnchantmentKey = createKey(CustomEnchantmentKey.CUSTOM_ENCHANTMENT.getKey());
        parentContainer.set(customEnchantmentKey, PersistentDataType.TAG_CONTAINER_ARRAY,
                childContainers.toArray(new PersistentDataContainer[0]));

        return meta;
    }

    /**
     * Get custom enchantments from item.
     *
     * @param item item to get custom enchantments from
     * @return simple custom enchantments list
     */
    public static List<SimpleCustomEnchantment> getCustomEnchantments(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey customEnchantmentKey = createKey(CustomEnchantmentKey.CUSTOM_ENCHANTMENT.getKey());

        if (container.has(customEnchantmentKey, PersistentDataType.TAG_CONTAINER_ARRAY)) {
            PersistentDataContainer[] childContainers = container
                    .get(customEnchantmentKey, PersistentDataType.TAG_CONTAINER_ARRAY);

            if (childContainers == null || childContainers.length == 0) {
                return null;
            }

            List<SimpleCustomEnchantment> simpleCustomEnchantments = new ArrayList<>();

            for (PersistentDataContainer childContainer : childContainers) {
                NamespacedKey idKey = createKey(CustomEnchantmentKey.CUSTOM_ENCHANTMENT_ID.getKey());
                NamespacedKey levelKey = createKey(CustomEnchantmentKey.CUSTOM_ENCHANTMENT_LEVEL.getKey());

                if (childContainer.has(idKey, PersistentDataType.STRING)
                        && childContainer.has(levelKey, PersistentDataType.SHORT)) {
                    String id = childContainer.get(idKey, PersistentDataType.STRING);
                    Short level = childContainer.get(levelKey, PersistentDataType.SHORT);

                    if (id != null && level != null) {
                        SimpleCustomEnchantment simpleCustomEnchantment = new SimpleCustomEnchantment(
                                CustomEnchantmentType.valueOf(id.toUpperCase()), level
                        );

                        simpleCustomEnchantments.add(simpleCustomEnchantment);
                    }
                }
            }

            return simpleCustomEnchantments;
        }

        return null;
    }

}
