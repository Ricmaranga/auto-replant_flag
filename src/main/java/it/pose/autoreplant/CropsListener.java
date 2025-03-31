package it.pose.autoreplant;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CropsListener implements Listener {

    private static final Map<String, Material> itemToBlock = Map.of(
            "POTATOES", Material.POTATO,
            "CARROTS", Material.CARROT,
            "BEETROOTS", Material.BEETROOT,
            "WHEAT", Material.WHEAT,
            "NETHER_WART", Material.NETHER_WART
    );

    public static Set<Material> ALLOWED_CROPS = new HashSet<>();

    @EventHandler
    public void onCropBreak(BlockBreakEvent e) {

        if (AutoReplant.AUTO_REPLANT_FLAG == null) return;

        Block block = e.getBlock();
        Material type = block.getType();

        if (!ALLOWED_CROPS.contains(itemToBlock.getOrDefault(type.toString(), type))) return;

        Location loc = block.getLocation();
        World world = loc.getWorld();
        if (world == null) return;

        RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();

        RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(world));
        if (regionManager == null) return;

        ApplicableRegionSet regions = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(loc));
        if (regions.size() == 0) return;

        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(e.getPlayer());
        StateFlag.State flagState = regions.queryState(localPlayer, AutoReplant.AUTO_REPLANT_FLAG);
        if (flagState != StateFlag.State.ALLOW) return;

        BlockData data = block.getBlockData();
        if (data instanceof Ageable ageable && ageable.getAge() == ageable.getMaximumAge() && AutoReplant.drop)
            block.getWorld().dropItemNaturally(loc, new ItemStack(itemToBlock.getOrDefault(type.toString(), type)));
        Bukkit.getScheduler().runTaskLater(AutoReplant.getInstance(), () -> {
            e.setCancelled(true);
            block.setType(type);
            block.setBlockData(data);
        }, AutoReplant.cooldown);

    }
}
