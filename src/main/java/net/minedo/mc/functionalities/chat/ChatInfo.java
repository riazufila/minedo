package net.minedo.mc.functionalities.chat;

public class ChatInfo {

    private boolean custom;
    private String selectedColor;

    public ChatInfo(boolean custom, String selectedColor) {
        this.custom = custom;
        this.selectedColor = selectedColor;
    }

    public boolean isCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public String getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(String selectedColor) {
        this.selectedColor = selectedColor;
    }

}
