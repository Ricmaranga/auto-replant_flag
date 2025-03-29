package it.pose.autoreplant;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionContainer;
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

import java.util.Set;

public class CropListener implements Listener {


    private static final Set<Material> ALLOWED_CROPS;
    static {
        ALLOWED_CROPS = Set.of(Material.WHEAT, Material.CARROTS, Material.POTATOES);
    }

    @EventHandler
    public void onCropBreak(BlockBreakEvent e) {

        if (AutoReplant.AUTO_REPLANT_FLAG == null) return;

        Block block = e.getBlock();
        Material type = block.getType();

        if (!ALLOWED_CROPS.contains(type)) return;

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
        e.setCancelled(true);
        if (data instanceof Ageable ageable && ageable.getAge() == ageable.getMaximumAge()) block.getWorld().dropItemNaturally(loc, new ItemStack(type));
        block.setType(type);
        block.setBlockData(data);

    }
}
