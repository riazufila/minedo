package net.minedo.mc.constants.command.type.nicknametype;

public enum NicknameType {

    SET("set"),
    REVEAL("reveal"),
    REMOVE("remove");

    private final String type;

    NicknameType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
