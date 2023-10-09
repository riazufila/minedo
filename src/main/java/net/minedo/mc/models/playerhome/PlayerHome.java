package net.minedo.mc.models.playerhome;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * Player home settings.
 *
 * @param name        name of home
 * @param worldType   world type of home
 * @param coordinateX coordinate x of home
 * @param coordinateY coordinate y of home
 * @param coordinateZ coordinate z of home
 */
public record PlayerHome(@NotNull String name,
                         @NotNull World worldType,
                         double coordinateX,
                         double coordinateY,
                         double coordinateZ) {
}
