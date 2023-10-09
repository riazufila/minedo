package net.minedo.mc.constants.grouppermission;

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
    GroupPermission(String permission) {
        this.permission = permission;
    }

    /**
     * Get permission nodes.
     *
     * @return permission
     */
    public String getPermission() {
        return permission;
    }


}
