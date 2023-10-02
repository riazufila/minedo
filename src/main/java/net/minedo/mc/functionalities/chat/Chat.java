package net.minedo.mc.functionalities.chat;

import net.minedo.mc.Minedo;
import net.minedo.mc.functionalities.chat.blocked.ChatBlocked;
import net.minedo.mc.functionalities.chat.box.ChatBox;
import net.minedo.mc.functionalities.chat.timeout.ChatTimeout;

public class Chat {

    private final Minedo pluginInstance;

    public Chat(Minedo pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    public void setupChat() {
        // Chat timeout.
        this.pluginInstance.getServer().getPluginManager().registerEvents(
                new ChatTimeout(this.pluginInstance), this.pluginInstance
        );

        // Chat box.
        this.pluginInstance.getServer().getPluginManager().registerEvents(
                new ChatBox(),
                this.pluginInstance
        );

        // Chat blocked.
        this.pluginInstance.getServer().getPluginManager().registerEvents(
                new ChatBlocked(),
                this.pluginInstance
        );
    }

}
