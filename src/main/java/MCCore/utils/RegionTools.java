package MCCore.utils;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Objects;

public class RegionTools {

    private static final WorldEdit worldEdit = WorldEdit.getInstance();
    private static final WorldGuard worldGuard = WorldGuard.getInstance();

    public static boolean isPlayerinRegion(Player p, String region){
        double x = p.getLocation().getX();
        double y = p.getLocation().getY();
        double z = p.getLocation().getZ();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        ProtectedRegion rg = container.get(BukkitAdapter.adapt(p.getWorld())).getRegion(region);
        if (rg != null && rg.contains(BlockVector3.at(x, y, z))){
            return true;
        }
        return false;
    }

    public static Location[] getRegionMinAndMaxPoints(World world, String region){
        if (world == null){
            return null;
        }
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        ProtectedRegion rg = container.get(BukkitAdapter.adapt(world)).getRegion(region);
        if (rg == null){
            return null;
        }
        Location min = new Location(world, rg.getMinimumPoint().getX(), rg.getMinimumPoint().getY(), rg.getMinimumPoint().getZ());
        Location max = new Location(world, rg.getMaximumPoint().getX(), rg.getMaximumPoint().getY(), rg.getMaximumPoint().getZ());

        return new Location[]{min, max};
    }

    public static Block[] getSelectedBlocks(Player player) {
        LocalSession worldEditSession = worldEdit.getSessionManager().findByName(player.getName());
        if (worldEditSession != null) {
            if(worldEditSession.getSelectionWorld() != null) {
                RegionSelector regionSelector = worldEditSession.getRegionSelector(worldEditSession.getSelectionWorld());
                if(regionSelector.isDefined()) {
                    try {
                        Region region = regionSelector.getRegion();
                        World world = Bukkit.getWorld(Objects.requireNonNull(region.getWorld()).getName());
                        Block[] blocks = new Block[(int) region.getVolume()];

                        int i = 0;
                        for(BlockVector3 blockVector3 : region) {
                            blocks[i] = new Location(world, blockVector3.getX(), blockVector3.getY(), blockVector3.getZ()).getBlock();
                            i++;
                        }

                        return blocks;
                    } catch (IncompleteRegionException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return new Block[0];
    }

    public static Location[] getPlayerSelection(Player player){
        LocalSession worldEditSession = worldEdit.getSessionManager().findByName(player.getName());
        if (worldEditSession == null || worldEditSession.getSelectionWorld() == null){
            return new Location[0];
        }
        RegionSelector regionSelector = worldEditSession.getRegionSelector(worldEditSession.getSelectionWorld());
        if (!regionSelector.isDefined()){
            return new Location[0];
        }

        try{
            if (worldEditSession.getSelection() instanceof CuboidRegion cRegion){
                World world = Bukkit.getWorld(Objects.requireNonNull(cRegion.getWorld()).getName());
                BlockVector3 pos1 = cRegion.getPos1();
                BlockVector3 pos2 = cRegion.getPos2();
                Location loc1 = new Location(world, pos1.getX(), pos1.getY(), pos1.getZ());
                Location loc2 = new Location(world, pos2.getX(), pos2.getY(), pos2.getZ());
                return new Location[]{loc1, loc2};
            }
            return new Location[0];
        }
        catch(IncompleteRegionException | NullPointerException e){
            return new Location[0];
        }
    }
}
