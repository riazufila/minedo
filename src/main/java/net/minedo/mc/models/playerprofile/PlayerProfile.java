package net.minedo.mc.models.playerprofile;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record PlayerProfile(int id, @NotNull UUID uuid, String nickname) {
}
