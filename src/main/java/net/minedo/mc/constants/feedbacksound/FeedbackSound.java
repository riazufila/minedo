package net.minedo.mc.constants.feedbacksound;

import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public enum FeedbackSound {

    INFO(Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1),
    ERROR(Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1),
    TELEPORT(Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1, 1),
    SKILL_NO_POINTS(Sound.BLOCK_LAVA_EXTINGUISH, 0.7f, 2.0f),
    LIGHTNING_SKILL_FAIL(Sound.ENTITY_GUARDIAN_ATTACK, 1, 2),
    REGION_LAUNCH_ENTITIES_ABOVE(Sound.BLOCK_AZALEA_LEAVES_STEP, 1, 1.6f),
    REGION_REGENERATING(Sound.BLOCK_AZALEA_LEAVES_PLACE, 1, 0.7f),
    EXPLOSION_SKILL(Sound.ENTITY_CREEPER_PRIMED, 1, 1),
    ICE_SKILL(Sound.ENTITY_GENERIC_EXPLODE, 1, 1.7f);

    private final Sound sound;
    private final float volume;
    private final float pitch;

    /**
     * Feedback sound.
     *
     * @param sound  sound
     * @param volume volume
     * @param pitch  pitch
     */
    FeedbackSound(@NotNull Sound sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    /**
     * Get sound.
     *
     * @return sound
     */
    public @NotNull Sound getSound() {
        return sound;
    }

    /**
     * Get volume.
     *
     * @return volume
     */
    public float getVolume() {
        return volume;
    }

    /**
     * Get pitch.
     *
     * @return pitch
     */
    public float getPitch() {
        return pitch;
    }

}
