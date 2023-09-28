package net.minedo.mc.models.playercolor;

public class PlayerColor {

    private int playerId;
    private String prefixPreset;
    private String prefixCustom;
    private String contentPreset;
    private String contentCustom;

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

    public String getPrefixCustom() {
        return prefixCustom;
    }

    public void setPrefixCustom(String prefixCustom) {
        this.prefixCustom = prefixCustom;
    }

    public String getContentPreset() {
        return contentPreset;
    }

    public void setContentPreset(String contentPreset) {
        this.contentPreset = contentPreset;
    }

    public String getContentCustom() {
        return contentCustom;
    }

    public void setContentCustom(String contentCustom) {
        this.contentCustom = contentCustom;
    }

}
