package net.minedo.mc.constants.chatcolorsymbol;

public enum ChatColorSymbol {

    GOLD_CHAT_SYMBOL('#'),
    GREEN_CHAT_SYMBOL('>'),
    TURQUOISE_CHAT_SYMBOL('~'),
    RED_CHAT_SYMBOL('<'),
    PURPLE_CHAT_SYMBOL('*');

    private final char symbol;

    ChatColorSymbol(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }

}
