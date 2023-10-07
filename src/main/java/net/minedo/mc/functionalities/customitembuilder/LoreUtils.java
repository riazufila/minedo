package net.minedo.mc.functionalities.customitembuilder;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class LoreUtils {

    public static List<String> sliceString(String input) {
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

    private static List<Component> buildLore(String lore, NamedTextColor color, TextDecoration decoration) {
        List<Component> components = new ArrayList<>();

        for (String slicedLoreText : LoreUtils.sliceString(lore)) {
            Component loreComponent = Component.text(slicedLoreText).decoration(TextDecoration.ITALIC, false);

            if (color != null) {
                loreComponent = loreComponent.color(color);
            }

            if (decoration != null) {
                loreComponent = loreComponent.decorate(decoration);
            }

            components.add(loreComponent);
        }

        return components;
    }

    public static List<Component> getLoreComponents(String lore, NamedTextColor color, TextDecoration decoration) {
        return buildLore(lore, color, decoration);
    }

    public static void updateLore(ItemMeta meta, Component component, boolean addNewLine) {
        if (meta.hasLore()) {
            List<Component> existingComponents = meta.lore();
            List<Component> newComponents = new ArrayList<>();

            if (addNewLine) {
                newComponents.add(Component.empty());
            }

            newComponents.add(component);
            assert existingComponents != null;
            existingComponents.addAll(newComponents);
            meta.lore(existingComponents);
        } else {
            meta.lore(Collections.singletonList(component));
        }
    }

    public static void updateLore(ItemMeta meta, List<Component> components, boolean addNewLine) {
        if (meta.hasLore()) {
            List<Component> existingComponents = meta.lore();
            List<Component> newComponents = new ArrayList<>();

            if (addNewLine) {
                newComponents.add(Component.empty());
            }

            newComponents.addAll(components);
            assert existingComponents != null;
            existingComponents.addAll(newComponents);
            meta.lore(existingComponents);
        } else {
            meta.lore(components);
        }
    }

}
