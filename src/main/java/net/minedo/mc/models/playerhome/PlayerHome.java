package net.minedo.mc.models.playerhome;

import org.bukkit.World;

public record PlayerHome(String name, World worldType, double coordinateX, double coordinateY, double coordinateZ) {
}
