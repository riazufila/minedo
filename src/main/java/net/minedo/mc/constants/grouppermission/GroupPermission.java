package net.minedo.mc.constants.grouppermission;

public enum GroupPermission {

    GOLD("minedo.group.gold"),
    EMERALD("minedo.group.emerald"),
    DIAMOND("minedo.group.diamond"),
    REDSTONE("minedo.group.redstone"),
    OBSIDIAN("minedo.group.obsidian");

    private final String permission;

    GroupPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }


}
