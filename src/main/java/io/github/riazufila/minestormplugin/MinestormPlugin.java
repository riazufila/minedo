package io.github.riazufila.minestormplugin;

import io.github.riazufila.minestormplugin.betteritem.BetterItem;
import io.github.riazufila.minestormplugin.customcommand.spawn.SpawnCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class MinestormPlugin extends JavaPlugin {

    private static MinestormPlugin instance;

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

    public static MinestormPlugin getInstance() {
        return instance;
    }

}
