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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class RegionUtils {

    private static final WorldEdit worldEdit = WorldEdit.getInstance();

    /**
     * Check if a given entity is in a region
     * @param entity the entity
     * @param region the region name
     * @return a boolean
     */
    public static boolean isEntityInRegion(@NotNull Entity entity, @NotNull String region){
        Location loc = entity.getLocation();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        ProtectedRegion rg = container.get(BukkitAdapter.adapt(loc.getWorld())).getRegion(region);
        return rg != null && rg.contains(BlockVector3.at(x, y, z));
    }


    /**
     * Get the minimum and maximum points of a region in a given world
     * @param world the world
     * @param region the region name
     * @return an array with the minimum and maximum points, respectively. Null if the region doesn't exist
     */
    public static @Nullable Location[] getRegionMinAndMaxPoints(@NotNull World world, @NotNull String region){
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        ProtectedRegion rg = container.get(BukkitAdapter.adapt(world)).getRegion(region);
        if (rg == null){
            return null;
        }
        Location min = new Location(world, rg.getMinimumPoint().x(), rg.getMinimumPoint().y(), rg.getMinimumPoint().z());
        Location max = new Location(world, rg.getMaximumPoint().x(), rg.getMaximumPoint().y(), rg.getMaximumPoint().z());

        return new Location[]{min, max};
    }

    /**
     * Get whether a player has a WorldEdit selection
     * @param player the player
     * @return a boolean
     */
    public boolean hasSelection(@NotNull Player player){
        LocalSession worldEditSession = worldEdit.getSessionManager().findByName(player.getName());
        if (worldEditSession == null) return false;
        com.sk89q.worldedit.world.World weWorld = worldEditSession.getSelectionWorld();
        if (weWorld == null) return false;
        RegionSelector regionSelector = worldEditSession.getRegionSelector(weWorld);
        return regionSelector != null && regionSelector.isDefined();
    }

    /**
     * Get the blocks within the player's selection
     * @param player the player
     * @return an array of blocks or null
     */
    public static @Nullable Block[] getSelectionBlocks(@NotNull Player player) {
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
        return null;
    }

    /**
     * Get the location at the center of a player's selection
     * @param player the player
     * @return a {@link Location} or null
     */
    public @Nullable Location getSelectionCenter(@NotNull Player player){
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

    /**
     * Get the entities within a player's selection
     * @param player the player
     * @return a collection of entities or null
     */
    public static @Nullable Collection<Entity> getSelectionEntities(@NotNull Player player){
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
        if (bounds == null) return null;
        for (Entity e : loc.getNearbyEntities(rg.getWidth(), rg.getHeight(), rg.getWidth())){
            if (isEntityWithinSelection(bounds[0], bounds[1], e)){
                containedEntities.add(e);
            }
        }
        return containedEntities;
    }

    /**
     * Get whether an entity is contained within a player's selection
     * @param player the player
     * @param entity the entity
     * @return a boolean
     */
    public static boolean isEntityWithinSelection(@NotNull Player player, @NotNull Entity entity){
        Location[] sel = getPlayerSelection(player);
        if (sel == null) return false;
        return isEntityWithinSelection(sel[0], sel[1], entity);
    }

    /**
     * Get whether an entity is contained within two location
     * @param bound1 the first location bound
     * @param bound2 the second location bound
     * @param entity the entity
     * @return a boolean
     */
    public static boolean isEntityWithinSelection(@NotNull Location bound1, @NotNull Location bound2, @NotNull Entity entity){
        Location loc = entity.getLocation();
        if (loc.x() < Math.min(bound1.x(), bound2.x()) || loc.x() > Math.max(bound1.x(), bound2.x())){
            return false;
        }
        if (loc.y() < Math.min(bound1.y(), bound2.y()) || loc.y() > Math.max(bound1.y(), bound2.y())){
            return false;
        }
        if (loc.z() < Math.min(bound1.z(), bound2.z()) || loc.z() > Math.max(bound1.z(), bound2.z())){
            return false;
        }
        return true;
    }

    /**
     * Get all blocks between two points
     * @param pointA the first bound
     * @param pointB the second bound
     * @return a list of blocks
     */
    public static @NotNull List<Block> getBlocksBetweenPoints(@NotNull Block pointA, @NotNull Block pointB){
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

    /**
     * Get the two positions defining a player's selection
     * @param player the player
     * @return an array of two {@link Location}s or null
     */
    public static @Nullable Location[] getPlayerSelection(@NotNull  Player player){
        LocalSession worldEditSession = worldEdit.getSessionManager().findByName(player.getName());
        if (worldEditSession == null || worldEditSession.getSelectionWorld() == null) {
            return null;
        }
        RegionSelector regionSelector = worldEditSession.getRegionSelector(worldEditSession.getSelectionWorld());
        if (!regionSelector.isDefined()) {
            return null;
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
            return null;

        }
        catch(IncompleteRegionException | NullPointerException e){
            return null;
        }
    }
}
