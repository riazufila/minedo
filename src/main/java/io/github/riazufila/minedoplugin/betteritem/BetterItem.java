package io.github.riazufila.minedoplugin.betteritem;

import io.github.riazufila.minedoplugin.MinedoPlugin;
import io.github.riazufila.minedoplugin.database.model.astralgear.AstralGear;
import io.github.riazufila.minedoplugin.database.model.astralgear.AstralGearAttribute;
import io.github.riazufila.minedoplugin.database.model.astralgear.AstralGearEnchantment;
import io.github.riazufila.minedoplugin.database.model.astralgear.AstralGearLore;
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

public class BetterItem implements Listener {

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

    public ItemStack buildItem(AstralGear astralGear) {
        ItemStack betterItem = new ItemStack(astralGear.getMaterial());
        ItemMeta meta = betterItem.getItemMeta();

        // Set NBT tag.
        NamespacedKey typeKey = new NamespacedKey(MinedoPlugin.getInstance(), "type");
        NamespacedKey subTypeKey = new NamespacedKey(MinedoPlugin.getInstance(), "subType");
        NamespacedKey uuidKey = new NamespacedKey(MinedoPlugin.getInstance(), "uuid");
        NamespacedKey timestampKey = new NamespacedKey(MinedoPlugin.getInstance(), "timestamp");
        meta.getPersistentDataContainer().set(typeKey, PersistentDataType.STRING, "ASTRAL_GEAR");
        meta.getPersistentDataContainer().set(subTypeKey, PersistentDataType.STRING, astralGear.getDisplayName());
        meta.getPersistentDataContainer().set(uuidKey, PersistentDataType.STRING, UUID.randomUUID().toString());
        meta.getPersistentDataContainer().set(timestampKey, PersistentDataType.STRING, Instant.now().toString());

        // Set display name.
        Component displayNameComponent = Component.text(astralGear.getDisplayName()).decoration(TextDecoration.ITALIC, false);

        if (astralGear.getColor() != null) {
            displayNameComponent = displayNameComponent.color(TextColor.fromHexString(astralGear.getColor()));
        }

        if (astralGear.getDecoration() != null) {
            displayNameComponent = displayNameComponent.decorate(astralGear.getDecoration());
        }

        meta.displayName(displayNameComponent);

        // Set lore.
        AstralGearLore astralGearLore = astralGear.getLore();
        List<Component> list = new ArrayList<>();

        for (String slicedLoreText : this.sliceString(astralGearLore.getText())) {
            Component loreComponent = Component.text(slicedLoreText).decoration(TextDecoration.ITALIC, false);

            if (astralGearLore.getColor() != null) {
                loreComponent = loreComponent.color(TextColor.fromHexString(astralGearLore.getColor()));
            }

            if (astralGearLore.getDecoration() != null) {
                loreComponent = loreComponent.decorate(astralGearLore.getDecoration());
            }

            list.add(loreComponent);
        }

        meta.lore(list);

        // Add enchantments.
        if (astralGear.getEnchantments() != null) {
            for (AstralGearEnchantment enchantment : astralGear.getEnchantments()) {
                meta.addEnchant(enchantment.enchantment, enchantment.level, true);
            }
        }

        // Add attributes.
        if (astralGear.getAttributes() != null) {
            for (AstralGearAttribute attribute : astralGear.getAttributes()) {
                meta.addAttributeModifier(attribute.attribute, new AttributeModifier(UUID.randomUUID(), attribute.attribute.toString(), attribute.modifier, attribute.operation, attribute.slot));
            }
        }

        // Set item with new metas.
        betterItem.setItemMeta(meta);

        return betterItem;
    }

    public ItemStack prepareItem() {
        Random random = new Random();

        // 50% chance to retrieve an item.
        if (random.nextBoolean()) {
            AstralGear[] astralGearList = AstralGear.getAllAstralGears();

            double[] probabilities = new double[astralGearList.length];
            int index = 0;

            for (AstralGear astralGear : astralGearList) {
                probabilities[index] = astralGear.getProbability().getProbability();
                index++;
            }

            // Randomly select one Astral Gear.
            UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_128_PP.create();
            AstralGear selectedAstralGear = new DiscreteProbabilityCollectionSampler<>(rng, Arrays.asList(astralGearList), probabilities).sample();

            return buildItem(selectedAstralGear);
        }

        return null;
    }

}
