package net.minedo.mc.functionalities.chat;

import net.minedo.mc.Minedo;
import net.minedo.mc.functionalities.chat.blocked.ChatBlocked;
import net.minedo.mc.functionalities.chat.box.ChatBox;
import net.minedo.mc.functionalities.chat.timeout.ChatTimeout;
import org.bukkit.plugin.PluginManager;

public class Chat {

    public void setupChat() {
        Minedo instance = Minedo.getInstance();
        PluginManager pluginManager = instance.getServer().getPluginManager();

        // Chat timeout.
        pluginManager.registerEvents(new ChatTimeout(), instance);

        // Chat box.
        pluginManager.registerEvents(new ChatBox(), instance);

        // Chat blocked.
        pluginManager.registerEvents(new ChatBlocked(), instance);
    }

}
