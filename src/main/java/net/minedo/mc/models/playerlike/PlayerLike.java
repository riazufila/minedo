package net.minedo.mc.models.playerlike;

import java.time.Duration;
import java.time.Instant;

public record PlayerLike(int likeReceivedCount, int likeSentCount, Instant lastLikeSent) {

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
