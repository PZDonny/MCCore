package MCCore.utils;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Gate;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class AbilityTools {


//Nearby Players at Entity w/o Message
    public static List<Player> getNearbyPlayers(Entity originEntity, double distance, Player... ignoredPlayers) {
        return getNearbyPlayers(originEntity, distance, null, ignoredPlayers);
    }

    //Nearby Players at Player w/ Message
    public static List<Player> getNearbyPlayers(Entity p, double distance, String errorPrefix, Player... ignoredPlayers) {
        Collection<Entity> coll = p.getNearbyEntities(distance, distance, distance);
        List<Player> players = new ArrayList<>();
        for(Entity e : coll){
            if (e.hasMetadata("NPC")){
                continue;
            }
            if (!(e instanceof Player)){
                continue;
            }
            if (e == p || Arrays.stream(ignoredPlayers).anyMatch(a -> e == a)){
                continue;
            }
            players.add((Player) e);
        }
        if (players.isEmpty()){
            if (errorPrefix != null && p instanceof Player player) {
                player.sendMessage(errorPrefix + ChatColor.RED + "You are not within " + distance + " blocks of any players!");
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
            }
        }
        return players;
    }



    public static List<Player> getNearbyPlayers(double d, Location location, Player... ignoredPlayers) {
        Collection<Entity> coll = location.getWorld().getNearbyEntities(location, d, d, d);
        List<Player> players = new ArrayList<>();
        if (location.getWorld().getNearbyEntities(location, d,d,d).stream().noneMatch(en->en instanceof Player)) {
            return players;
        }
        for(Entity e : coll){
            if (e.hasMetadata("NPC")) continue;
            if (!(e instanceof Player)) continue;
            if (Arrays.stream(ignoredPlayers).anyMatch(a -> e == a)){
                continue;
            }
            players.add((Player) e);
        }
        return players;
    }

    //Nearby Players at Location w/o Message

    public static List<Player> getNearbyPlayers(Player p, double d, Location location, Player... ignoredPlayers) {
        return getNearbyPlayers(p, d, location, null, ignoredPlayers);
    }

//Nearby Players at Location w/ Message
    public static List<Player> getNearbyPlayers(Player p, double d, Location location, String prefix, Player... ignoredPlayers) {
        Collection<Entity> coll = p.getWorld().getNearbyEntities(location, d, d, d);
        List<Player> players = new ArrayList<>();
        for(Entity e : coll){
            if (e.hasMetadata("NPC")) continue;
            if (!(e instanceof Player)) continue;
            if (e == p || Arrays.stream(ignoredPlayers).anyMatch(a -> e == a)){
                continue;
            }
            players.add((Player) e);
        }

        if (players.isEmpty() && prefix != null){
            p.sendMessage(prefix+ChatColor.RED+"There are not any players within "+d+" blocks!");
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
        }
        return players;
    }

//Get Targeted player
    public static Player returnTargetPlayer(Player p, double distance, PassthroughType passthroughType, String prefix, Player... ignoredPlayers) {
        Location loc = p.getEyeLocation();
        Vector v = p.getEyeLocation().getDirection();
        RayTraceResult result = p.getWorld().rayTraceBlocks(loc, v, distance, FluidCollisionMode.NEVER, true);
        Block targetedBlockExact = null;
        if (result != null) targetedBlockExact = result.getHitBlock();
        Material exactMaterial = null;
        float t = 0f;
        double exactDist = -1;
        if (targetedBlockExact != null){
            exactDist = targetedBlockExact.getLocation().distanceSquared(loc);
            exactMaterial = targetedBlockExact.getType();
        }

        while (t < (float) (distance)){
            t += 0.2f;
            double x = v.getX() * t;
            double y = v.getY() * t;
            double z = v.getZ() * t;
            Location locClone = loc.clone().add(x,y,z);

            //Passthrough Checks
            Block block = locClone.getBlock();
            double blockDist = block.getLocation().distanceSquared(loc);
            Material material = block.getType();
            if (blockDist > exactDist && exactDist != -1){
                break;
            }

            boolean checkOpenables = false;
            switch (passthroughType){
                //Air
                case AIR_ONLY -> {
                    if (!(block.getType() == Material.AIR || material == Material.VOID_AIR || material == Material.CAVE_AIR)){
                        denyPassthough(p, prefix);
                        return null;
                    }
                }
                //Passables
                case PASSABLE_ONLY, PASSABLE_AND_WATER, PASSABLE_AND_FLUIDS, PASSABLE_AND_LAVA -> {
                    //Lava Checks
                    if (material == Material.LAVA){
                        if (!(passthroughType == PassthroughType.PASSABLE_AND_FLUIDS || passthroughType == PassthroughType.PASSABLE_AND_LAVA)){
                            denyPassthough(p, prefix);
                            return null;
                        }
                    }
                    //Water Checks
                    else if (material == Material.WATER){
                        if (!(passthroughType == PassthroughType.PASSABLE_AND_FLUIDS || passthroughType == PassthroughType.PASSABLE_AND_WATER)){
                            denyPassthough(p, prefix);
                            return null;
                        }
                    }

                    //Semi-Passable Block Checks
                    if (!block.isPassable()){
                        //Gate Checks
                        if (Items.isGate(material)) {
                            checkOpenables = true;
                        }
                        //Other "Non-Passable"
                        else {
                            //Bukkit.broadcast(Component.text("SEMI-PASSABLE | "+material.name()));
                            //Player can be standing in front of a closed door/trapdoor and still get detected
                            if (block.getBlockData() instanceof Openable && material != Material.BARREL){
                                if (block.getBoundingBox().contains(locClone.toVector())){
                                    //Bukkit.broadcast(Component.text("DENIED VECTOR OPENABLE"));
                                    denyPassthough(p, prefix);
                                    return null;
                                }
                                checkOpenables = true;
                            }
                            else{
                                if (block.equals(targetedBlockExact)){
                                    denyPassthough(p, prefix);
                                    return null;
                                }
                            }
                        }
                    }
                }
                case ALL_BUT_WATER, ALL_BUT_LAVA, ALL_BUT_FLUIDS -> {
                    //Lava Checks
                    if (material == Material.LAVA){
                        if (!(passthroughType == PassthroughType.ALL_BUT_FLUIDS || passthroughType == PassthroughType.ALL_BUT_LAVA)){
                            if (block.getBoundingBox().contains(locClone.toVector())){
                                //Bukkit.broadcast(Component.text("DENIED VECTOR BOUNDS LAVA"));
                                denyPassthough(p, prefix);
                                return null;
                            }
                        }
                    }
                    //Water Checks
                    else if (material == Material.WATER){
                        if (!(passthroughType == PassthroughType.ALL_BUT_FLUIDS || passthroughType == PassthroughType.ALL_BUT_WATER)){
                            if (block.getBoundingBox().contains(locClone.toVector())){
                                //Bukkit.broadcast(Component.text("DENIED VECTOR BOUNDS WATER"));
                                denyPassthough(p, prefix);
                                return null;
                            }
                        }
                    }
                }
            }

            Collection<Entity> nearby = locClone.getWorld().getNearbyEntities(locClone, 0.15, 0.15, 0.15);
            if (!nearby.isEmpty()){
                Entity[] arr = nearby.toArray(new Entity[nearby.size()]);
                for (Entity e : arr) {
                    if (e instanceof Player && !e.equals(p) && !PlayerUtils.isNPC((Player) e)) {
                        if (checkOpenables){
                            double x2 = e.getLocation().getX();
                            double y2 = e.getLocation().getY();
                            double z2 = e.getLocation().getZ();
                            Location eBoundLoc = new Location(e.getWorld(), x2, y2, z2);
                            double eBoundDist = eBoundLoc.distanceSquared(loc);

                            double x1 = block.getBoundingBox().getCenterX();
                            double y1 = block.getBoundingBox().getCenterY();
                            double z1 = block.getBoundingBox().getCenterZ();
                            Location vecBlockBoundLoc = new Location(p.getWorld(), x1, y1, z1);
                            double vecBlockBoundDist = vecBlockBoundLoc.distanceSquared(loc);

                            if (Items.isGate(material)){
                                Openable data = (Openable) block.getBlockData();
                                if (!data.isOpen() && (eBoundDist > vecBlockBoundDist && exactDist != -1 && eBoundDist > exactDist)){
                                    denyPassthough(p, prefix);
                                    return null;
                                }
                            }
                            //Player is behind block (like a closed door, in the same block as the door)
                            if (vecBlockBoundDist < exactDist){
                                denyPassthough(p, prefix);
                                return null;
                            }

                            else if (block.getBoundingBox().contains(e.getBoundingBox()) && (eBoundDist > vecBlockBoundDist && exactDist != -1 && eBoundDist > exactDist)){
                                //Bukkit.broadcast(Component.text("DENIED BOUND BOX"));
                                denyPassthough(p, prefix);
                                return null;
                            }
                        }
                        return (Player) e;
                    }
                }
            }
        }
        if (prefix != null){
            p.sendMessage(prefix+ChatColor.RED+"Target a player within "+ChatColor.YELLOW+distance+ChatColor.RED+" blocks of your location!");
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
        }
        return null;
    }

//Get Targeted Player w/o Message
    public static Player returnTargetPlayer(Player p, double distance, PassthroughType passthroughType, Player... ignoredPlayers) {
        return returnTargetPlayer(p, distance, passthroughType, null, ignoredPlayers);
    }



    public static boolean passesPassthroughCheck(Location location, PassthroughType passthroughType) {
        return passesPassthroughCheck(location, passthroughType, null, null);
    }

    public static boolean passesPassthroughCheck(Location location, PassthroughType passthroughType, Player player, String prefix) {
        Block block = location.getBlock();
        Material material = block.getType();
        boolean checkOpenables = false;
        switch (passthroughType){
            //Air
            case AIR_ONLY -> {
                if (!(block.getType() == Material.AIR || material == Material.VOID_AIR || material == Material.CAVE_AIR)){
                    denyPassthough(player, prefix);
                    return false;
                }
            }
            //Passables
            case PASSABLE_ONLY, PASSABLE_AND_WATER, PASSABLE_AND_FLUIDS, PASSABLE_AND_LAVA -> {
                //Lava Checks
                if (material == Material.LAVA){
                    if (!(passthroughType == PassthroughType.PASSABLE_AND_FLUIDS || passthroughType == PassthroughType.PASSABLE_AND_LAVA)){
                        denyPassthough(player, prefix);
                        return false;
                    }
                }
                //Water Checks
                else if (material == Material.WATER){
                    if (!(passthroughType == PassthroughType.PASSABLE_AND_FLUIDS || passthroughType == PassthroughType.PASSABLE_AND_WATER)){
                        denyPassthough(player, prefix);
                        return false;
                    }
                }

                //Semi-Passable Block Checks
                if (!block.isPassable()){
                    //Gate Checks
                    if (Items.isGate(material) && !(((Gate) block.getBlockData()).isOpen())) {
                        if (!passesBoundingBoxCheck(block, location)){
                            denyPassthough(player, prefix);
                            return false;
                        }

                    }
                    //Other "Non-Passable"
                    else {
                        if (!passesBoundingBoxCheck(block, location)){
                            denyPassthough(player, prefix);
                            return false;
                        }
                        /*
                        //Bukkit.broadcast(Component.text("SEMI-PASSABLE | "+material.name()));
                        //Player can be standing in front of a closed door/trapdoor and still get detected
                        if (block.getBlockData() instanceof Openable && material != Material.BARREL){
                            if (!passesBoundingBoxCheck(block, location)){
                                denyPassthough(player, prefix);
                                return false;
                            }
                            checkOpenables = true;
                        }
                        else{
                            denyPassthough(player, prefix);
                            return false;
                        }*/
                    }
                }
            }
            case ALL_BUT_WATER, ALL_BUT_LAVA, ALL_BUT_FLUIDS -> {
                //Lava Checks
                if (material == Material.LAVA){
                    if (!(passthroughType == PassthroughType.ALL_BUT_FLUIDS || passthroughType == PassthroughType.ALL_BUT_LAVA)){
                        if (block.getBoundingBox().contains(location.toVector())){
                            //Bukkit.broadcast(Component.text("DENIED VECTOR BOUNDS LAVA"));
                            denyPassthough(player, prefix);
                            return false;
                        }
                    }
                }
                //Water Checks
                else if (material == Material.WATER){
                    if (!(passthroughType == PassthroughType.ALL_BUT_FLUIDS || passthroughType == PassthroughType.ALL_BUT_WATER)){
                        if (block.getBoundingBox().contains(location.toVector())){
                            //Bukkit.broadcast(Component.text("DENIED VECTOR BOUNDS WATER"));
                            denyPassthough(player, prefix);
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private static boolean passesBoundingBoxCheck(Block block, Location location){
        return !block.getBoundingBox().contains(location.toVector());
    }


    private static void denyPassthough(Player p, String prefix){
        if (p == null ||prefix == null){
            return;
        }
        p.sendMessage(prefix+ChatColor.RED+"You cannot target a player through that block with this ability!");
        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
    }
    public enum PassthroughType {
        AIR_ONLY, //AIR
        PASSABLE_ONLY, //PASSABLE BLOCKS w/o FLUIDS
        PASSABLE_AND_WATER, //PASSABLE WITH WATER w/o LAVA
        PASSABLE_AND_LAVA, //PASSABLE WITH LAVA w/o WATER
        PASSABLE_AND_FLUIDS, //PASSABLE WITH LAVA AND WATER
        ALL, //PASS THROUGH ALL BLOCKS
        ALL_BUT_WATER, //PASS THROUGH ALL BLOCKS BUT WATER
        ALL_BUT_LAVA, //PASS THROUGH ALL BLOCKS BUT LAVA
        ALL_BUT_FLUIDS; // PASS THROUGH ALL BLOCKS BUT WATER AND LAVA
    }

}
