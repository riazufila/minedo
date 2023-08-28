package io.github.riazufila.minedoplugin;

import io.github.riazufila.minedoplugin.itembuilder.ItemBuilder;
import io.github.riazufila.minedoplugin.customcommand.spawn.SpawnCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class MinedoPlugin extends JavaPlugin {

    private static MinedoPlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        // Populate newly generated chests with Better Items.
        getServer().getPluginManager().registerEvents(new ItemBuilder(), this);

        // Spawn command and listeners.
        SpawnCommand spawnCommand = new SpawnCommand();
        getServer().getPluginManager().registerEvents(spawnCommand, this);
        Objects.requireNonNull(getCommand("spawn")).setExecutor(spawnCommand);
    }

    public static MinedoPlugin getInstance() {
        return instance;
    }

}
