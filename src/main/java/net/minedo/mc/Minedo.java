package net.minedo.mc;

import net.minedo.mc.customevents.caller.PlayerNonBlockInteractCaller;
import net.minedo.mc.functionalities.chat.Chat;
import net.minedo.mc.functionalities.customcommand.CustomCommand;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentWrapper;
import net.minedo.mc.functionalities.customitembuilder.CustomItemBuilder;
import net.minedo.mc.functionalities.joinleavebroadcast.JoinLeaveBroadcast;
import net.minedo.mc.functionalities.player.PlayerProfileManager;
import net.minedo.mc.functionalities.regionregeneration.RegionRegeneration;
import net.minedo.mc.functionalities.spawnlocationinitializer.SpawnLocationInitializer;
import net.minedo.mc.models.region.Region;
import net.minedo.mc.repositories.regionrepository.RegionRepository;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Minedo extends JavaPlugin {

    private static Minedo instance;

    public static @NotNull Minedo getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        PluginManager pluginManager = this.getServer().getPluginManager();

        // Set spawn location.
        SpawnLocationInitializer spawnLocationInitializer = new SpawnLocationInitializer();
        if (!spawnLocationInitializer.hasSpawnLocationSet()) {
            spawnLocationInitializer.setSpawnLocation();
        }

        // Player profile.
        pluginManager.registerEvents(new PlayerProfileManager(), this);

        // Join and leave broadcast message.
        pluginManager.registerEvents(new JoinLeaveBroadcast(), this);

        // Populate newly generated chests with custom items.
        pluginManager.registerEvents(new CustomItemBuilder(), this);

        // Custom enchantments.
        CustomEnchantmentWrapper customEnchantmentWrapper = new CustomEnchantmentWrapper();
        customEnchantmentWrapper.registerCustomEnchantments();
        pluginManager.registerEvents(customEnchantmentWrapper, this);

        // Custom commands.
        CustomCommand customCommand = new CustomCommand();
        customCommand.setupCustomCommands();

        // Region regenerations.
        List<Region> regions = RegionRepository.getAllRegions();

        if (regions != null) {
            for (Region region : regions) {
                RegionRegeneration regionRegeneration = new RegionRegeneration(region);

                if (!regionRegeneration.getRegionSnapshot()) {
                    regionRegeneration.setRegionSnapshot();
                }

                pluginManager.registerEvents(regionRegeneration, this);
            }
        }

        // Chat.
        Chat chat = new Chat();
        chat.setupChat();

        // Custom events caller.
        pluginManager.registerEvents(new PlayerNonBlockInteractCaller(), this);
    }

}
