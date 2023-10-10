package net.minedo.mc.constants.command.feedbacksound;

import org.bukkit.Sound;

public enum FeedbackSound {

    INFO(Sound.BLOCK_NOTE_BLOCK_BELL),
    ERROR(Sound.BLOCK_NOTE_BLOCK_BASS);

    private final Sound sound;

    /**
     * Feedback sound.
     *
     * @param sound sound
     */
    FeedbackSound(Sound sound) {
        this.sound = sound;
    }

    /**
     * Get sound.
     *
     * @return sound
     */
    public Sound getSound() {
        return sound;
    }


}
