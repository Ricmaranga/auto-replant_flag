package it.pose.autoreplant;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.List;

public class AutoReplant extends JavaPlugin implements Listener {

    public CropsLoader cropsLoader;
    public static StateFlag AUTO_REPLANT_FLAG;
    private static AutoReplant instance;
    public static List<String> cropsStrings;
    public static Integer cooldown;
    public static Boolean drop;

    @Override
    public void onEnable() {

        instance = this;

        getCommand("autoreplant").setExecutor(new Commands(this));

        if (AUTO_REPLANT_FLAG == null) {
            getLogger().severe("AutoReplant flag not initialized. Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        File cropsFile = new File (this.getDataFolder(), "crops.yml");
        File configFile = new File(this.getDataFolder(), "config.yml");

        if(!cropsFile.exists()){ saveResource("crops.yml", false); }
        if(!configFile.exists()){ saveResource("config.yml", false); }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        config.options().parseComments(true);
        FileConfiguration crops = YamlConfiguration.loadConfiguration(cropsFile);
        crops.options().parseComments(true);

        cropsStrings = crops.getStringList("crops");
        cooldown = config.getInt("cooldown");
        drop = config.getBoolean("dropItem");

        getLogger().info(cropsStrings.toString());

        getServer().getPluginManager().registerEvents(new CropsListener(), this);

        cropsLoader = new CropsLoader();
        cropsLoader.LoadCrops();

    }

    @Override
    public void onLoad() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            StateFlag flag = new StateFlag("auto-replant", false);
            registry.register(flag);
            AUTO_REPLANT_FLAG = flag;
            getLogger().info("AutoReplant flag registered.");
        } catch (FlagConflictException e) {
            Flag<?> existing = registry.get("auto-replant");
            if (existing instanceof StateFlag) {
                AUTO_REPLANT_FLAG = (StateFlag) existing;
                getLogger().info("AutoReplant flag already registered.");
                getLogger().info(existing.getClass().getName());
            } else {
                getLogger().severe("Conflicting flag 'auto-replant' is not a StateFlag. Disabling plugin.");
            }
        }
    }

    public static AutoReplant getInstance(){
        return instance;
    }

    public void reload() {

        File configFile = new File(getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        cooldown = config.getInt("cooldown");
        drop = config.getBoolean("dropItem");

        File cropsFile = new File(getDataFolder(), "crops.yml");
        FileConfiguration cropsConfig = YamlConfiguration.loadConfiguration(cropsFile);
        cropsStrings = cropsConfig.getStringList("crops");



        cropsLoader.LoadCrops();

        getLogger().info("Configuration reloaded!");
    }


}