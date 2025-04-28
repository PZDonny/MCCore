package net.donnypz.mccore.utils.misc;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class RegionUtils {

    private static final WorldEdit worldEdit = WorldEdit.getInstance();

    public static boolean isEntityInRegion(Entity entity, String region){
        double x = entity.getLocation().getX();
        double y = entity.getLocation().getY();
        double z = entity.getLocation().getZ();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        ProtectedRegion rg = container.get(BukkitAdapter.adapt(entity.getWorld())).getRegion(region);
        return rg != null && rg.contains(BlockVector3.at(x, y, z));
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
        Location min = new Location(world, rg.getMinimumPoint().x(), rg.getMinimumPoint().y(), rg.getMinimumPoint().z());
        Location max = new Location(world, rg.getMaximumPoint().x(), rg.getMaximumPoint().y(), rg.getMaximumPoint().z());

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
                            blocks[i] = new Location(world, blockVector3.x(), blockVector3.y(), blockVector3.z()).getBlock();
                            i++;
                        }

                        return blocks;
                    } catch (IncompleteRegionException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return new Block[0];
    }

    public Location getSelectionCenter(Player player){
        LocalSession worldEditSession = worldEdit.getSessionManager().findByName(player.getName());
        if (worldEditSession == null || worldEditSession.getSelectionWorld() == null){
            return null;
        }
        RegionSelector regionSelector = worldEditSession.getRegionSelector(worldEditSession.getSelectionWorld());
        if (!regionSelector.isDefined()) {
            return null;
        }

        Region rg = regionSelector.getIncompleteRegion();
        World w = BukkitAdapter.adapt(rg.getWorld());
        Vector3 center = rg.getCenter();
        double x = center.x();
        double y = center.y();
        double z = center.z();
        return new Location(w, x, y, z);
    }

    public static Collection<Entity> getContainedEntities(Player player){
        LocalSession worldEditSession = worldEdit.getSessionManager().findByName(player.getName());
        if (worldEditSession == null || worldEditSession.getSelectionWorld() == null){
            return null;
        }
        RegionSelector regionSelector = worldEditSession.getRegionSelector(worldEditSession.getSelectionWorld());
        if (!regionSelector.isDefined()) {
            return null;
        }

        Region rg = regionSelector.getIncompleteRegion();
        World w = BukkitAdapter.adapt(rg.getWorld());
        Vector3 center = rg.getCenter();
        double x = center.x();
        double y = center.y();
        double z = center.z();
        Location loc = new Location(w, x, y, z);
        ArrayList<Entity> containedEntities = new ArrayList<>();
        Location[] bounds = getPlayerSelection(player);
        for (Entity e : loc.getNearbyEntities(rg.getWidth(), rg.getHeight(), rg.getWidth())){
            if (isEntityWithinSelection(bounds, e)){
                containedEntities.add(e);
            }
        }
        return containedEntities;
    }

    public static boolean isEntityWithinSelection(Player player, Entity entity){
        return isEntityWithinSelection(getPlayerSelection(player), entity);
    }
    
    public static boolean isEntityWithinSelection(Location[] bounds, Entity entity){
        Location loc = entity.getLocation();
        if (loc.x() < Math.min(bounds[0].x(), bounds[1].x()) || loc.x() > Math.max(bounds[0].x(), bounds[1].x())){
            return false;
        }
        if (loc.y() < Math.min(bounds[0].y(), bounds[1].y()) || loc.y() > Math.max(bounds[0].y(), bounds[1].y())){
            return false;
        }
        if (loc.z() < Math.min(bounds[0].z(), bounds[1].z()) || loc.z() > Math.max(bounds[0].z(), bounds[1].z())){
            return false;
        }
        return true;
    }
    
    public static List<Block> getBlocksBetweenPoints(Block pointA, Block pointB){
        List<Block> blocks = new ArrayList<>();
        int minX = Math.min(pointA.getX(), pointB.getX());
        int maxX = Math.max(pointA.getX(), pointB.getX());

        int minY = Math.min(pointA.getY(), pointB.getY());
        int maxY = Math.max(pointA.getY(), pointB.getY());

        int minZ = Math.min(pointA.getZ(), pointB.getZ());
        int maxZ = Math.max(pointA.getZ(), pointB.getZ());
        World w = pointA.getWorld();
        
        for (int i = minX; i <= maxX; i++){
            for (int j = minY; j <= maxY; j++){
                for (int k = minZ; k <= maxZ; k++){
                    blocks.add(w.getBlockAt(i, j, k));
                }
            }
        }
        return blocks;
    }

    public static Location[] getPlayerSelection(Player player){
        LocalSession worldEditSession = worldEdit.getSessionManager().findByName(player.getName());
        if (worldEditSession == null || worldEditSession.getSelectionWorld() == null) {
            return new Location[0];
        }
        RegionSelector regionSelector = worldEditSession.getRegionSelector(worldEditSession.getSelectionWorld());
        if (!regionSelector.isDefined()) {
            return new Location[0];
        }

        try{
            if (worldEditSession.getSelection() instanceof CuboidRegion cRegion){
                World world = Bukkit.getWorld(Objects.requireNonNull(cRegion.getWorld()).getName());
                BlockVector3 pos1 = cRegion.getPos1();
                BlockVector3 pos2 = cRegion.getPos2();
                Location loc1 = new Location(world, pos1.x(), pos1.y(), pos1.z());
                Location loc2 = new Location(world, pos2.x(), pos2.y(), pos2.z());
                return new Location[]{loc1, loc2};
            }
            return new Location[0];

        }
        catch(IncompleteRegionException | NullPointerException e){
            return new Location[0];
        }
    }
}
