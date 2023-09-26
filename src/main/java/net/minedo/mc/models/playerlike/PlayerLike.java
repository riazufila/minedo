package net.minedo.mc.models.playerlike;

import java.time.Duration;
import java.time.Instant;

public class PlayerLike {

    private int playerId;
    private int likeReceivedCount;
    private int likeSentCount;
    private Instant lastLikeSent;

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getLikeReceivedCount() {
        return likeReceivedCount;
    }

    public void setLikeReceivedCount(int likeReceivedCount) {
        this.likeReceivedCount = likeReceivedCount;
    }

    public int getLikeSentCount() {
        return likeSentCount;
    }

    public void setLikeSentCount(int likeSentCount) {
        this.likeSentCount = likeSentCount;
    }

    public Instant getLastLikeSent() {
        return lastLikeSent;
    }

    public void setLastLikeSent(Instant lastLikeSent) {
        this.lastLikeSent = lastLikeSent;
    }

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
