package net.minedo.mc.functionalities.chat;

import net.minedo.mc.Minedo;
import net.minedo.mc.functionalities.chat.color.ChatColor;
import net.minedo.mc.functionalities.chat.prefix.ChatPrefix;
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

        // Chat color.
        this.pluginInstance.getServer().getPluginManager().registerEvents(
                new ChatColor(),
                this.pluginInstance
        );

        // Chat prefix.
        this.pluginInstance.getServer().getPluginManager().registerEvents(
                new ChatPrefix(),
                this.pluginInstance
        );
    }

}
