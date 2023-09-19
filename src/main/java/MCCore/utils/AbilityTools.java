package MCCore.utils;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class AbilityTools {


//Nearby Players at Player w/o Message
    public static List<Player> getNearbyPlayers(Entity originEntity, double distance, Player... ignoredPlayers){
        Collection<Entity> coll = originEntity.getNearbyEntities(distance, distance, distance);
        List<Player> players = new ArrayList<>();
        if (originEntity.getNearbyEntities(distance,distance,distance).stream().noneMatch(en->en instanceof Player)) {
            return players;
        }
        Entity[] toPlayer = coll.toArray(new Entity[coll.size()]);
        for(Entity e : toPlayer){
            if (e.hasMetadata("NPC")) continue;
            if (!(e instanceof Player)) continue;
            if (e.equals(originEntity) || Arrays.stream(ignoredPlayers).anyMatch(a -> e == a)) continue;
            players.add((Player) e);
        }
        return players;
    }

    //Nearby Players at Player w/ Message
    public static List<Player> getNearbyPlayers(Player p, double distance, String prefix, Player... ignoredPlayers){
        Collection<Entity> coll = p.getNearbyEntities(distance, distance, distance);
        List<Player> players = new ArrayList<>();
        if (p.getNearbyEntities(distance,distance,distance).stream().noneMatch(en->en instanceof Player)) {
            p.sendMessage(prefix+ ChatColor.RED+"You are not within "+distance+" blocks of any players!");
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
            return players;
        }
        Entity[] toPlayer = coll.toArray(new Entity[coll.size()]);
        for(Entity e : toPlayer){
            if (e.hasMetadata("NPC")) continue;
            if (!(e instanceof Player)) continue;
            if (e == p || Arrays.stream(ignoredPlayers).anyMatch(a -> e == a)) continue;
            players.add((Player) e);
        }
        return players;
    }

//Nearby Players at Location w/o Message
    public static List<Player> getNearbyPlayers(Entity p, double d, Location l, Player... ignoredPlayers){
        Collection<Entity> coll = p.getWorld().getNearbyEntities(l, d, d, d);
        List<Player> players = new ArrayList<>();
        if (p.getWorld().getNearbyEntities(l, d,d,d).stream().noneMatch(en->en instanceof Player)) {
            return players;
        }
        Entity[] toPlayer = coll.toArray(new Entity[coll.size()]);
        for(Entity e : toPlayer){
            if (e.hasMetadata("NPC")) continue;
            if (!(e instanceof Player)) continue;
            if (e == p || Arrays.stream(ignoredPlayers).anyMatch(a -> e == a)) continue;
            players.add((Player) e);
        }
        return players;
    }

//Nearby Players at Location w/ Message
    public static List<Player> getNearbyPlayers(Player p, double d, Location l, String prefix, Player... ignoredPlayers){
        Collection<Entity> coll = p.getWorld().getNearbyEntities(l, d, d, d);
        List<Player> players = new ArrayList<>();
        if (p.getWorld().getNearbyEntities(l, d,d,d).stream().noneMatch(en->en instanceof Player)) {
            p.sendMessage(prefix+ChatColor.RED+"There are not any players within "+d+" blocks!");
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
            return players;
        }
        Entity[] toPlayer = coll.toArray(new Entity[coll.size()]);
        for(Entity e : toPlayer){
            if (e.hasMetadata("NPC")) continue;
            if (!(e instanceof Player)) continue;
            if (e == p || Arrays.stream(ignoredPlayers).anyMatch(a -> e == a)) continue;
            players.add((Player) e);
        }
        return players;
    }

//Get Targeted Player w/o Message
    public static Player returnTargetPlayer(Player p, double distance, PassthroughType passthroughType, Player... ignoredPlayers) {
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
                        return null;
                    }
                }
                //Passables
                case PASSABLE_ONLY, PASSABLE_AND_WATER, PASSABLE_AND_FLUIDS, PASSABLE_AND_LAVA -> {
                    //Lava Checks
                    if (material == Material.LAVA){
                        if (!(passthroughType == PassthroughType.PASSABLE_AND_FLUIDS || passthroughType == PassthroughType.PASSABLE_AND_LAVA)){
                            return null;
                        }
                    }
                    //Water Checks
                    else if (material == Material.WATER){
                        if (!(passthroughType == PassthroughType.PASSABLE_AND_FLUIDS || passthroughType == PassthroughType.PASSABLE_AND_WATER)){
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
                                    return null;
                                }
                                checkOpenables = true;
                            }
                            else{
                                if (block.equals(targetedBlockExact)){
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
                                return null;
                            }
                        }
                    }
                    //Water Checks
                    else if (material == Material.WATER){
                        if (!(passthroughType == PassthroughType.ALL_BUT_FLUIDS || passthroughType == PassthroughType.ALL_BUT_WATER)){
                            if (block.getBoundingBox().contains(locClone.toVector())){
                                //Bukkit.broadcast(Component.text("DENIED VECTOR BOUNDS WATER"));
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
                    if (e instanceof Player && !e.equals(p) && !PlayerTools.isNPC((Player) e)) {
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
                                    return null;
                                }
                            }
                            //Player is behind block (like a closed door, in the same block as the door)
                            if (vecBlockBoundDist < exactDist){
                                return null;
                            }

                            else if (block.getBoundingBox().contains(e.getBoundingBox()) && (eBoundDist > vecBlockBoundDist && exactDist != -1 && eBoundDist > exactDist)){
                                //Bukkit.broadcast(Component.text("DENIED BOUND BOX"));
                                return null;
                            }
                        }
                        return (Player) e;
                    }
                }
            }
        }
        return null;
    }

    //Get Targeted Player w/ Message
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
                    if (e instanceof Player && !e.equals(p) && !PlayerTools.isNPC((Player) e)) {
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
        p.sendMessage(prefix+ChatColor.RED+"Target a player within "+ChatColor.YELLOW+distance+ChatColor.RED+" blocks of your location!");
        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
        return null;
    }

    private static void denyPassthough(Player p, String prefix){
        p.sendMessage(prefix+ChatColor.RED+"You cannot target a player through that block with this ability!");
        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
    }
    public enum PassthroughType {
        AIR_ONLY, //AIR
        PASSABLE_ONLY, //PASSABLE BLOCKS w/o WATER and LAVA
        PASSABLE_AND_WATER, //PASSABLE WITH WATER w/o LAVA
        PASSABLE_AND_LAVA, //PASSABLE WITH LAVA w/o WATER
        PASSABLE_AND_FLUIDS, //PASSABLE WITH LAVA AND WATER
        ALL, //PASS THROUGH ALL BLOCKS
        ALL_BUT_WATER, //PASS THROUGH ALL BLOCKS BUT WATER
        ALL_BUT_LAVA, //PASS THROUGH ALL BLOCKS BUT LAVA
        ALL_BUT_FLUIDS; // PASS THROUGH ALL BLOCKS BUT WATER AND LAVA
    }

}
