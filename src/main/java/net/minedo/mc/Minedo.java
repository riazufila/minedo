package net.minedo.mc;

import net.minedo.mc.functionalities.chat.Chat;
import net.minedo.mc.functionalities.customcommand.CustomCommand;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentManager;
import net.minedo.mc.functionalities.customitembuilder.CustomItemBuilder;
import net.minedo.mc.functionalities.joinleavebroadcast.JoinLeaveBroadcast;
import net.minedo.mc.functionalities.player.PlayerProfileManager;
import net.minedo.mc.functionalities.regionregeneration.RegionRegeneration;
import net.minedo.mc.functionalities.spawnlocationinitializer.SpawnLocationInitializer;
import net.minedo.mc.models.region.Region;
import net.minedo.mc.repositories.regionrepository.RegionRepository;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class Minedo extends JavaPlugin {

    private static Minedo instance;

    public static Minedo getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        // Set spawn location.
        SpawnLocationInitializer spawnLocationInitializer = new SpawnLocationInitializer();
        if (!spawnLocationInitializer.hasSpawnLocationSet()) {
            spawnLocationInitializer.setSpawnLocation();
        }

        // Player profile.
        this.getServer().getPluginManager().registerEvents(new PlayerProfileManager(), this);

        // Join and leave broadcast message.
        this.getServer().getPluginManager().registerEvents(new JoinLeaveBroadcast(), this);

        // Populate newly generated chests with custom items.
        this.getServer().getPluginManager().registerEvents(new CustomItemBuilder(), this);

        // Custom enchantments.
        CustomEnchantmentManager customEnchantmentManager = new CustomEnchantmentManager();
        customEnchantmentManager.registerEvents();

        // Custom commands.
        CustomCommand customCommand = new CustomCommand();
        customCommand.setupCustomCommands();

        // Region regenerations.
        RegionRepository regionRepository = new RegionRepository();
        List<Region> regions = RegionRepository.getAllRegions();

        for (Region region : regions) {
            RegionRegeneration regionRegeneration = new RegionRegeneration(region);

            if (!regionRegeneration.getRegionSnapshot()) {
                regionRegeneration.setRegionSnapshot();
            }

            this.getServer().getPluginManager().registerEvents(regionRegeneration, this);
        }

        // Chat.
        Chat chat = new Chat();
        chat.setupChat();
    }

}
