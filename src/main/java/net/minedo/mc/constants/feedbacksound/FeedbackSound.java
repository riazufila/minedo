package net.minedo.mc.constants.feedbacksound;

import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public enum FeedbackSound {

    INFO(Sound.BLOCK_NOTE_BLOCK_BELL),
    ERROR(Sound.BLOCK_NOTE_BLOCK_BASS),
    TELEPORT(Sound.ITEM_CHORUS_FRUIT_TELEPORT),
    SKILL_NO_POINTS(Sound.BLOCK_LAVA_EXTINGUISH);

    private final Sound sound;

    /**
     * Feedback sound.
     *
     * @param sound sound
     */
    FeedbackSound(@NotNull Sound sound) {
        this.sound = sound;
    }

    /**
     * Get sound.
     *
     * @return sound
     */
    public @NotNull Sound getSound() {
        return sound;
    }

}
