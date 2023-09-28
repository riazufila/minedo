package net.minedo.mc.models.playercolor;

public class PlayerColor {

    private int playerId;
    private String prefixPreset;
    private String contentPreset;

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getPrefixPreset() {
        return prefixPreset;
    }

    public void setPrefixPreset(String prefixPreset) {
        this.prefixPreset = prefixPreset;
    }

    public String getContentPreset() {
        return contentPreset;
    }

    public void setContentPreset(String contentPreset) {
        this.contentPreset = contentPreset;
    }

}
