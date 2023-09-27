package net.minedo.mc;

import com.sk89q.worldedit.WorldEdit;
import net.minedo.mc.constants.worldtype.WorldType;
import net.minedo.mc.functionalities.chat.Chat;
import net.minedo.mc.functionalities.customcommand.CustomCommand;
import net.minedo.mc.functionalities.itembuilder.ItemBuilder;
import net.minedo.mc.functionalities.joinleavebroadcast.JoinLeaveBroadcast;
import net.minedo.mc.functionalities.player.PlayerProfileManager;
import net.minedo.mc.functionalities.regionregeneration.RegionRegeneration;
import net.minedo.mc.functionalities.spawnlocationinitializer.SpawnLocationInitializer;
import net.minedo.mc.models.region.Region;
import net.minedo.mc.repositories.regionrepository.RegionRepository;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class Minedo extends JavaPlugin {

    @Override
    public void onEnable() {
        // Set spawn location.
        SpawnLocationInitializer spawnLocationInitializer = new SpawnLocationInitializer(this);
        if (!spawnLocationInitializer.hasSpawnLocationSet()) {
            spawnLocationInitializer.setSpawnLocation();
        }

        // Player profile.
        this.getServer().getPluginManager().registerEvents(new PlayerProfileManager(), this);

        // Join and leave broadcast message.
        this.getServer().getPluginManager().registerEvents(new JoinLeaveBroadcast(), this);

        // Populate newly generated chests with Better Items.
        this.getServer().getPluginManager().registerEvents(new ItemBuilder(this), this);

        // Custom commands.
        CustomCommand customCommand = new CustomCommand(this);
        customCommand.setupCustomCommands();

        // Region regenerations.
        RegionRepository regionRepository = new RegionRepository(this);
        List<Region> regions = regionRepository.getAllRegions();

        for (Region region : regions) {
            RegionRegeneration regionRegeneration = new RegionRegeneration(region, this);

            if (!regionRegeneration.getRegionSnapshot()) {
                regionRegeneration.setRegionSnapshot();
            }

            this.getServer().getPluginManager().registerEvents(regionRegeneration, this);
        }

        // Chat.
        Chat chat = new Chat(this);
        chat.setupChat();
    }

    public World getWorldBasedOnName(String name) {
        return Bukkit.getWorld(name);
    }

    public World getOverworld() {
        return Bukkit.getWorld(WorldType.WORLD.getType());
    }

    public World getNetherWorld() {
        return Bukkit.getWorld(WorldType.NETHER_WORLD.getType());
    }

    public World getTheEndWorld() {
        return Bukkit.getWorld(WorldType.THE_END_WORLD.getType());
    }

    public List<World> getAllWorlds() {
        return Bukkit.getWorlds();
    }

    public WorldEdit getWorldEdit() {
        return WorldEdit.getInstance();
    }

}
