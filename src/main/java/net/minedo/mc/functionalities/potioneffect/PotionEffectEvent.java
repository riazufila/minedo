package net.minedo.mc.functionalities.potioneffect;

import org.bukkit.potion.PotionEffectType;

import java.time.Instant;

public class PotionEffectEvent {

    private final PotionEffectType potionEffectType;
    private final int duration;
    private final int amplifier;
    private final Instant dateTimePotionEffectGranted;

    public PotionEffectEvent(PotionEffectType potionEffectType, int duration, int amplifier) {
        this.potionEffectType = potionEffectType;
        this.duration = duration;
        this.amplifier = amplifier;
        this.dateTimePotionEffectGranted = Instant.now();
    }

    public PotionEffectType getPotionEffectType() {
        return potionEffectType;
    }

    public int getDuration() {
        return duration;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public Instant getDateTimePotionEffectGranted() {
        return dateTimePotionEffectGranted;
    }

}
