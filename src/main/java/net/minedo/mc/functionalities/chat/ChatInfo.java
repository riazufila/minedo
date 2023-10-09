package net.minedo.mc.functionalities.chat;

import org.jetbrains.annotations.Nullable;

/**
 * Chat info.
 *
 * @param custom        whether color is custom
 * @param selectedColor selected color
 */
public record ChatInfo(boolean custom,
                       @Nullable String selectedColor) {
}
