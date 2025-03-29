package it.pose.autoreplant;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;

public class AutoReplant extends JavaPlugin implements Listener {

    public static StateFlag AUTO_REPLANT_FLAG;

    @Override
    public void onEnable() {
        if (AUTO_REPLANT_FLAG == null) {
            getLogger().severe("AutoReplant flag not initialized. Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getServer().getPluginManager().registerEvents(new CropListener(), this);
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
}