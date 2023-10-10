package net.minedo.mc.functionalities.dataembedder;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minedo.mc.Minedo;
import net.minedo.mc.constants.customenchantment.key.CustomEnchantmentKey;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.common.utils.RomanUtils;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantment;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentWrapper;
import net.minedo.mc.functionalities.customitembuilder.LoreUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Uses PDC to store and get arbitrary data from within objects.
 */
public final class DataEmbedder {

    /**
     * Create a key using PDC.
     *
     * @param key used as an identifier when setting and getting a value
     * @return key object
     */
    public static @NotNull NamespacedKey createKey(@NotNull String key) {
        return new NamespacedKey(Minedo.getInstance(), key);
    }

    /**
     * Apply custom enchantments into item.
     *
     * @param meta               item meta to apply data into
     * @param customEnchantments simplified custom enchantment list
     */
    public static void applyCustomEnchantments(
            @NotNull ItemMeta meta, @NotNull List<CustomEnchantment> customEnchantments
    ) {
        PersistentDataContainer parentContainer = meta.getPersistentDataContainer();
        List<PersistentDataContainer> childContainers = new ArrayList<>();

        if (customEnchantments.isEmpty()) {
            return;
        }

        for (CustomEnchantment customEnchantment : customEnchantments) {
            PersistentDataContainer newContainer = parentContainer.getAdapterContext().newPersistentDataContainer();
            NamespacedKey idKey = createKey(CustomEnchantmentKey.CUSTOM_ENCHANTMENT_ID.getKey());
            NamespacedKey levelKey = createKey(CustomEnchantmentKey.CUSTOM_ENCHANTMENT_LEVEL.getKey());

            newContainer.set(idKey, PersistentDataType.STRING,
                    customEnchantment.getCustomEnchantmentType().toString().toLowerCase());
            newContainer.set(levelKey, PersistentDataType.SHORT, customEnchantment.getLevel());

            short level = customEnchantment.getLevel();
            String loreText = String.format(
                    level > 1 ? "%s %s" : "%s",
                    CustomEnchantmentWrapper
                            .formatCustomEnchantmentName(customEnchantment.getCustomEnchantmentType().toString()),
                    level > 1 ? RomanUtils.toRoman(customEnchantment.getLevel()) : null
            );

            Component customEnchantmentLore = Component
                    .text(loreText)
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false);

            LoreUtils.updateLore(meta, customEnchantmentLore, false);

            childContainers.add(newContainer);
        }

        NamespacedKey customEnchantmentKey = createKey(CustomEnchantmentKey.CUSTOM_ENCHANTMENT.getKey());
        parentContainer.set(customEnchantmentKey, PersistentDataType.TAG_CONTAINER_ARRAY,
                childContainers.toArray(new PersistentDataContainer[0]));
    }

    /**
     * Get custom enchantments from item.
     *
     * @param item item to get custom enchantments from
     * @return custom enchantments list
     */
    public static @Nullable List<CustomEnchantment> getCustomEnchantments(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey customEnchantmentKey = createKey(CustomEnchantmentKey.CUSTOM_ENCHANTMENT.getKey());

        if (container.has(customEnchantmentKey, PersistentDataType.TAG_CONTAINER_ARRAY)) {
            PersistentDataContainer[] childContainers = container
                    .get(customEnchantmentKey, PersistentDataType.TAG_CONTAINER_ARRAY);

            if (childContainers == null || childContainers.length == 0) {
                return null;
            }

            List<CustomEnchantment> customEnchantments = new ArrayList<>();

            for (PersistentDataContainer childContainer : childContainers) {
                NamespacedKey idKey = createKey(CustomEnchantmentKey.CUSTOM_ENCHANTMENT_ID.getKey());
                NamespacedKey levelKey = createKey(CustomEnchantmentKey.CUSTOM_ENCHANTMENT_LEVEL.getKey());

                if (childContainer.has(idKey, PersistentDataType.STRING)
                        && childContainer.has(levelKey, PersistentDataType.SHORT)) {
                    String id = childContainer.get(idKey, PersistentDataType.STRING);
                    Short level = childContainer.get(levelKey, PersistentDataType.SHORT);

                    if (id != null && level != null) {
                        CustomEnchantment customEnchantment = new CustomEnchantment(
                                CustomEnchantmentType.valueOf(id.toUpperCase()), level
                        );

                        customEnchantments.add(customEnchantment);
                    }
                }
            }

            return customEnchantments;
        }

        return null;
    }

}
