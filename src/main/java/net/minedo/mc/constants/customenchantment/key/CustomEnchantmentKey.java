package net.minedo.mc.constants.customenchantment.key;

public enum CustomEnchantmentKey {

    CUSTOM_ENCHANTMENT("custom-enchantment"),
    CUSTOM_ENCHANTMENT_ID("id"),
    CUSTOM_ENCHANTMENT_LEVEL("lvl");

    private final String key;

    CustomEnchantmentKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }


}
