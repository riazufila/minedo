package net.minedo.mc.constants.grouppermission;

import org.jetbrains.annotations.NotNull;

/**
 * Permission.
 */
public enum GroupPermission {

    GOLD("minedo.group.gold"),
    EMERALD("minedo.group.emerald"),
    DIAMOND("minedo.group.diamond"),
    REDSTONE("minedo.group.redstone"),
    OBSIDIAN("minedo.group.obsidian");

    private final String permission;

    /**
     * Permission.
     *
     * @param permission permission
     */
    GroupPermission(@NotNull String permission) {
        this.permission = permission;
    }

    /**
     * Get permission nodes.
     *
     * @return permission
     */
    public @NotNull String getPermission() {
        return permission;
    }


}
