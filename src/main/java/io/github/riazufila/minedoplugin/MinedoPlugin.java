package io.github.riazufila.minedoplugin;

import io.github.riazufila.minedoplugin.betteritem.BetterItem;
import io.github.riazufila.minedoplugin.customcommand.spawn.SpawnCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class MinedoPlugin extends JavaPlugin {

    private static MinedoPlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        // Populate newly generated chests with Astral Gears.
        getServer().getPluginManager().registerEvents(new BetterItem(), this);

        // Spawn command and listeners.
        SpawnCommand spawnCommand = new SpawnCommand();
        getServer().getPluginManager().registerEvents(spawnCommand, this);
        Objects.requireNonNull(getCommand("spawn")).setExecutor(spawnCommand);
    }

    public static MinedoPlugin getInstance() {
        return instance;
    }

}
