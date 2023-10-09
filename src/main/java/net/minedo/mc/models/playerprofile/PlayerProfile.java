package net.minedo.mc.models.playerprofile;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Player profile details.
 *
 * @param id       player id
 * @param uuid     player uuid
 * @param nickname player nickname
 */
public record PlayerProfile(int id,
                            @NotNull UUID uuid,
                            @Nullable String nickname) {
}
