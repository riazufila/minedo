package net.minedo.mc.models.playerblockedlist;

public class PlayerBlocked {

    private int playerId;
    private int blockedPlayerId;

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getBlockedPlayerId() {
        return blockedPlayerId;
    }

    public void setBlockedPlayerId(int blockedPlayerId) {
        this.blockedPlayerId = blockedPlayerId;
    }

}
