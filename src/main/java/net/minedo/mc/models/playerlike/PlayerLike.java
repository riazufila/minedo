package net.minedo.mc.models.playerlike;

import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;

/**
 * Statistics for player's likes.
 *
 * @param likeReceivedCount total likes received
 * @param likeSentCount     total likes sent
 * @param lastLikeSent      last like sent
 */
public record PlayerLike(int likeReceivedCount,
                         int likeSentCount,
                         @Nullable Instant lastLikeSent) {

    public boolean isLikeSentRecently() {
        if (this.lastLikeSent == null) {
            return false;
        }

        Instant currentInstant = Instant.now();
        Duration duration = Duration.between(this.lastLikeSent, currentInstant);
        int ONE_DAY_IN_SECONDS = 86400;

        return duration.getSeconds() <= ONE_DAY_IN_SECONDS;
    }

}
