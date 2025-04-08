package net.donnypz.mccore.utils;

import net.donnypz.mccore.utils.item.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Gate;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AbilityUtils {


//Nearby Players at Entity w/o Message
    public static List<Player> getNearbyPlayers(Entity entity, double distance, Player... ignoredPlayers) {
        return getNearbyPlayers(entity, distance, null, ignoredPlayers);
    }

    //Nearby Players at Player w/ Message

    /**
     * Get the players near an entity.
     * @param entity the entity to search around
     * @param distance the distance to check
     * @param errorPrefix the prefix for the error message. Null for no message
     * @param ignoredPlayers players to ignore
     * @return a list of found players
     */
    public static List<Player> getNearbyPlayers(Entity entity, double distance, Component errorPrefix, Player... ignoredPlayers) {
        Collection<Player> coll = entity.getLocation().getNearbyPlayers(distance);
        List<Player> players = new ArrayList<>();
        for(Player p : coll){
            if (p.hasMetadata("NPC")){
                continue;
            }
            if (p == entity || Arrays.stream(ignoredPlayers).anyMatch(ignored -> p == ignored)){
                continue;
            }
            players.add(p);
        }
        if (players.isEmpty() && errorPrefix != null){
            if (entity instanceof Player player) {
                player.sendMessage(errorPrefix.append(Component.text("You are not within "+distance+"blocks of any players!", NamedTextColor.RED)));
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
            }
        }
        return players;
    }



    public static List<Player> getNearbyPlayers(double distance, Location location, Player... ignoredPlayers) {
        List<Player> players = new ArrayList<>();
        for (Player p : location.getNearbyPlayers(distance)){
            if (p.hasMetadata("NPC")){
                continue;
            }
            if (Arrays.stream(ignoredPlayers).anyMatch(ignored -> p == ignored)){
                continue;
            }
            players.add(p);
        }
        return players;
    }

    //Nearby Players at Location w/o Message

    public static List<Player> getNearbyPlayers(Player p, double distance, Location location, Player... ignoredPlayers) {
        return getNearbyPlayers(p, distance, location, null, ignoredPlayers);
    }

//Nearby Players at Location w/ Message

    /**
     * Get the players near a player.
     * @param p the player searching
     * @param distance the distance to check
     * @param location the location to search around
     * @param errorPrefix the prefix for the error message. Null for no message
     * @param ignoredPlayers players to ignore
     * @return a list of found players
     */
    public static List<Player> getNearbyPlayers(Player p, double distance, Location location, Component errorPrefix, Player... ignoredPlayers) {
        Collection<Entity> coll = p.getWorld().getNearbyEntities(location, distance, distance, distance);
        List<Player> players = new ArrayList<>();
        for(Entity e : coll){
            if (e.hasMetadata("NPC")){
                continue;
            }
            if (!(e instanceof Player)) continue;
            if (e == p || Arrays.stream(ignoredPlayers).anyMatch(a -> e == a)){
                continue;
            }
            players.add((Player) e);
        }

        if (players.isEmpty() && errorPrefix != null){
            p.sendMessage(errorPrefix.append(Component.text("There are not any players within "+distance+"blocks", NamedTextColor.RED)));
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
        }
        return players;
    }


//Get Targeted player
    public static Player getTargetPlayer(Player p, double distance, PassthroughType passthroughType, @Nullable Component errorPrefix, Player... ignoredPlayers) {
        Location loc = p.getEyeLocation();
        Vector v = p.getEyeLocation().getDirection();
        RayTraceResult result = p.getWorld().rayTraceBlocks(loc, v, distance, FluidCollisionMode.NEVER, true);
        Block targetedBlockExact = null;
        if (result != null){
            targetedBlockExact = result.getHitBlock();
        }
        float t = 0f;
        double exactDist = -1;
        if (targetedBlockExact != null){
            exactDist = targetedBlockExact.getLocation().distanceSquared(loc);
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
                        denyPassthough(p, errorPrefix);
                        return null;
                    }
                }
                //Passables
                case PASSABLE_ONLY, PASSABLE_AND_WATER, PASSABLE_AND_FLUIDS, PASSABLE_AND_LAVA -> {
                    //Lava Checks
                    if (material == Material.LAVA){
                        if (!(passthroughType == PassthroughType.PASSABLE_AND_FLUIDS || passthroughType == PassthroughType.PASSABLE_AND_LAVA)){
                            denyPassthough(p, errorPrefix);
                            return null;
                        }
                    }
                    //Water Checks
                    else if (material == Material.WATER){
                        if (!(passthroughType == PassthroughType.PASSABLE_AND_FLUIDS || passthroughType == PassthroughType.PASSABLE_AND_WATER)){
                            denyPassthough(p, errorPrefix);
                            return null;
                        }
                    }

                //Semi-Passable Block Checks
                    if (!block.isPassable()){

                        //Gate Checks
                        if (ItemUtils.isGate(material)) {
                            checkOpenables = true;
                        }

                        //Other "Non-Passable"
                        else {
                            //Bukkit.broadcast(Component.text("SEMI-PASSABLE | "+material.name()));
                            //Player can be standing in front of a closed door/trapdoor and still get detected
                            if (block.getBlockData() instanceof Openable && material != Material.BARREL){
                                if (block.getBoundingBox().contains(locClone.toVector())){
                                    //Bukkit.broadcast(Component.text("DENIED VECTOR OPENABLE"));
                                    denyPassthough(p, errorPrefix);
                                    return null;
                                }
                                checkOpenables = true;
                            }
                            else{
                                if (block.equals(targetedBlockExact)){
                                    denyPassthough(p, errorPrefix);
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
                                denyPassthough(p, errorPrefix);
                                return null;
                            }
                        }
                    }
                    //Water Checks
                    else if (material == Material.WATER){
                        if (!(passthroughType == PassthroughType.ALL_BUT_FLUIDS || passthroughType == PassthroughType.ALL_BUT_WATER)){
                            if (block.getBoundingBox().contains(locClone.toVector())){
                                //Bukkit.broadcast(Component.text("DENIED VECTOR BOUNDS WATER"));
                                denyPassthough(p, errorPrefix);
                                return null;
                            }
                        }
                    }
                }
            }

            for (Entity e : locClone.getWorld().getNearbyEntities(locClone, 0.15, 0.15, 0.15, filter -> filter instanceof Player)) {
                if (!e.equals(p) && !EntityUtils.isNPC(e)) {
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

                        if (ItemUtils.isGate(material)){
                            Openable data = (Openable) block.getBlockData();
                            if (!data.isOpen() && (eBoundDist > vecBlockBoundDist && exactDist != -1 && eBoundDist > exactDist)){
                                denyPassthough(p, errorPrefix);
                                return null;
                            }
                        }
                        //Player is behind block (like a closed door, in the same block as the door)
                        if (vecBlockBoundDist < exactDist){
                            denyPassthough(p, errorPrefix);
                            return null;
                        }

                        else if (block.getBoundingBox().contains(e.getBoundingBox()) && (eBoundDist > vecBlockBoundDist && exactDist != -1 && eBoundDist > exactDist)){
                            //Bukkit.broadcast(Component.text("DENIED BOUND BOX"));
                            denyPassthough(p, errorPrefix);
                            return null;
                        }
                    }
                    return (Player) e;
                }
            }
        }
        if (errorPrefix != null){
            p.sendMessage(errorPrefix.append(MiniMessage.miniMessage().deserialize("<red>Target a player within <yellow>"+distance+"<red> blocks of your location!")));
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
        }
        return null;
    }

//Get Targeted Player w/o Message
    public static Player getTargetPlayer(Player p, double distance, PassthroughType passthroughType, Player... ignoredPlayers) {
        return getTargetPlayer(p, distance, passthroughType, null, ignoredPlayers);
    }

    public static Set<Entity> raytraceEntities(Location location, double distance, PassthroughType passthroughType, boolean ignoreDead, EntityType... ignoredEntities){
        Set<Entity> set = new HashSet<>();
        Vector v = location.getDirection();
        RayTraceResult result = location.getWorld().rayTraceBlocks(location, v, distance, FluidCollisionMode.NEVER, true);
        Block targetedBlockExact = null;
        if (result != null){
            targetedBlockExact = result.getHitBlock();
        }
        float t = 0f;
        double exactDist = -1;
        if (targetedBlockExact != null){
            exactDist = targetedBlockExact.getLocation().distanceSquared(location);
        }

        while (t < (float) (distance)){
            t += 0.2f;
            double x = v.getX() * t;
            double y = v.getY() * t;
            double z = v.getZ() * t;
            Location locClone = location.clone().add(x,y,z);

            //Passthrough Checks
            Block block = locClone.getBlock();
            double blockDist = block.getLocation().distanceSquared(location);
            Material material = block.getType();
            if (blockDist > exactDist && exactDist != -1){
                break;
            }

            boolean checkOpenables = false;
            switch (passthroughType){
                //Air
                case AIR_ONLY -> {
                    if (!(block.getType() == Material.AIR || material == Material.VOID_AIR || material == Material.CAVE_AIR)){
                        return set;
                    }
                }
                //Passables
                case PASSABLE_ONLY, PASSABLE_AND_WATER, PASSABLE_AND_FLUIDS, PASSABLE_AND_LAVA -> {
                    //Lava Checks
                    if (material == Material.LAVA){
                        if (!(passthroughType == PassthroughType.PASSABLE_AND_FLUIDS || passthroughType == PassthroughType.PASSABLE_AND_LAVA)){
                            return set;
                        }
                    }
                    //Water Checks
                    else if (material == Material.WATER){
                        if (!(passthroughType == PassthroughType.PASSABLE_AND_FLUIDS || passthroughType == PassthroughType.PASSABLE_AND_WATER)){
                            return set;
                        }
                    }

                    //Semi-Passable Block Checks
                    if (!block.isPassable()){
                        //Gate Checks
                        if (ItemUtils.isGate(material)) {
                            checkOpenables = true;
                        }
                        //Other "Non-Passable"
                        else {
                            //Bukkit.broadcast(Component.text("SEMI-PASSABLE | "+material.name()));
                            //Player can be standing in front of a closed door/trapdoor and still get detected
                            if (block.getBlockData() instanceof Openable && material != Material.BARREL){
                                if (block.getBoundingBox().contains(locClone.toVector())){
                                    return set;
                                }
                                checkOpenables = true;
                            }
                            else{
                                if (block.equals(targetedBlockExact)){
                                    return set;
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
                                return set;
                            }
                        }
                    }
                    //Water Checks
                    else if (material == Material.WATER){
                        if (!(passthroughType == PassthroughType.ALL_BUT_FLUIDS || passthroughType == PassthroughType.ALL_BUT_WATER)){
                            if (block.getBoundingBox().contains(locClone.toVector())){
                                return set;
                            }
                        }
                    }
                }
            }


            List<EntityType> ignoredTypes = Arrays.stream(ignoredEntities).toList();
            for (Entity e : locClone.getWorld().getNearbyEntities(locClone, 0.15, 0.15, 0.15, filter -> !ignoredTypes.contains(filter.getType()))) {
                if (ignoreDead && e instanceof LivingEntity le && le.isDead()){
                    continue;
                }
                if (!EntityUtils.isNPC(e)) {
                    if (checkOpenables){
                        double x2 = e.getLocation().getX();
                        double y2 = e.getLocation().getY();
                        double z2 = e.getLocation().getZ();
                        Location eBoundLoc = new Location(e.getWorld(), x2, y2, z2);
                        double eBoundDist = eBoundLoc.distanceSquared(location);

                        double x1 = block.getBoundingBox().getCenterX();
                        double y1 = block.getBoundingBox().getCenterY();
                        double z1 = block.getBoundingBox().getCenterZ();
                        Location vecBlockBoundLoc = new Location(location.getWorld(), x1, y1, z1);
                        double vecBlockBoundDist = vecBlockBoundLoc.distanceSquared(location);

                        if (ItemUtils.isGate(material)){
                            Openable data = (Openable) block.getBlockData();
                            if (!data.isOpen() && (eBoundDist > vecBlockBoundDist && exactDist != -1 && eBoundDist > exactDist)){
                                return set;
                            }
                        }
                        //Player is behind block (like a closed door, in the same block as the door)
                        if (vecBlockBoundDist < exactDist){
                            return set;
                        }

                        else if (block.getBoundingBox().contains(e.getBoundingBox()) && (eBoundDist > vecBlockBoundDist && exactDist != -1 && eBoundDist > exactDist)){
                            return set;
                        }
                    }
                    set.add(e);
                }
            }
        }
        return set;
    }


    public static Entity getTargetEntity(Player targeter, double distance, PassthroughType passthroughType, Component errorPrefix, boolean ignoreDead, EntityType... ignoredEntities) {
        Location loc = targeter.getEyeLocation();
        Vector v = targeter.getEyeLocation().getDirection();
        RayTraceResult result = targeter.getWorld().rayTraceBlocks(loc, v, distance, FluidCollisionMode.NEVER, true);
        Block targetedBlockExact = null;
        if (result != null){
            targetedBlockExact = result.getHitBlock();
        }
        float t = 0f;
        double exactDist = -1;
        if (targetedBlockExact != null){
            exactDist = targetedBlockExact.getLocation().distanceSquared(loc);
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
                        denyPassthoughEntity(targeter, errorPrefix);
                        return null;
                    }
                }
                //Passables
                case PASSABLE_ONLY, PASSABLE_AND_WATER, PASSABLE_AND_FLUIDS, PASSABLE_AND_LAVA -> {
                    //Lava Checks
                    if (material == Material.LAVA){
                        if (!(passthroughType == PassthroughType.PASSABLE_AND_FLUIDS || passthroughType == PassthroughType.PASSABLE_AND_LAVA)){
                            denyPassthoughEntity(targeter, errorPrefix);
                            return null;
                        }
                    }
                    //Water Checks
                    else if (material == Material.WATER){
                        if (!(passthroughType == PassthroughType.PASSABLE_AND_FLUIDS || passthroughType == PassthroughType.PASSABLE_AND_WATER)){
                            denyPassthoughEntity(targeter, errorPrefix);
                            return null;
                        }
                    }

                    //Semi-Passable Block Checks
                    if (!block.isPassable()){
                        //Gate Checks
                        if (ItemUtils.isGate(material)) {
                            checkOpenables = true;
                        }
                        //Other "Non-Passable"
                        else {
                            //Bukkit.broadcast(Component.text("SEMI-PASSABLE | "+material.name()));
                            //Player can be standing in front of a closed door/trapdoor and still get detected
                            if (block.getBlockData() instanceof Openable && material != Material.BARREL){
                                if (block.getBoundingBox().contains(locClone.toVector())){
                                    //Bukkit.broadcast(Component.text("DENIED VECTOR OPENABLE"));
                                    denyPassthoughEntity(targeter, errorPrefix);
                                    return null;
                                }
                                checkOpenables = true;
                            }
                            else{
                                if (block.equals(targetedBlockExact)){
                                    denyPassthoughEntity(targeter, errorPrefix);
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
                                denyPassthoughEntity(targeter, errorPrefix);
                                return null;
                            }
                        }
                    }
                    //Water Checks
                    else if (material == Material.WATER){
                        if (!(passthroughType == PassthroughType.ALL_BUT_FLUIDS || passthroughType == PassthroughType.ALL_BUT_WATER)){
                            if (block.getBoundingBox().contains(locClone.toVector())){
                                //Bukkit.broadcast(Component.text("DENIED VECTOR BOUNDS WATER"));
                                denyPassthoughEntity(targeter, errorPrefix);
                                return null;
                            }
                        }
                    }
                }
            }


            List<EntityType> ignoredTypes = Arrays.stream(ignoredEntities).toList();
            for (Entity e : locClone.getWorld().getNearbyEntities(locClone, 0.15, 0.15, 0.15, filter -> !ignoredTypes.contains(filter.getType()))) {
                if (ignoreDead && e instanceof LivingEntity le && le.isDead()){
                    continue;
                }
                if (!e.equals(targeter) && !EntityUtils.isNPC(e)) {
                    if (checkOpenables){
                        double x2 = e.getLocation().getX();
                        double y2 = e.getLocation().getY();
                        double z2 = e.getLocation().getZ();
                        Location eBoundLoc = new Location(e.getWorld(), x2, y2, z2);
                        double eBoundDist = eBoundLoc.distanceSquared(loc);

                        double x1 = block.getBoundingBox().getCenterX();
                        double y1 = block.getBoundingBox().getCenterY();
                        double z1 = block.getBoundingBox().getCenterZ();
                        Location vecBlockBoundLoc = new Location(targeter.getWorld(), x1, y1, z1);
                        double vecBlockBoundDist = vecBlockBoundLoc.distanceSquared(loc);

                        if (ItemUtils.isGate(material)){
                            Openable data = (Openable) block.getBlockData();
                            if (!data.isOpen() && (eBoundDist > vecBlockBoundDist && exactDist != -1 && eBoundDist > exactDist)){
                                denyPassthoughEntity(targeter, errorPrefix);
                                return null;
                            }
                        }
                        //Player is behind block (like a closed door, in the same block as the door)
                        if (vecBlockBoundDist < exactDist) {
                            denyPassthoughEntity(targeter, errorPrefix);
                            return null;
                        }

                        else if (block.getBoundingBox().contains(e.getBoundingBox()) && (eBoundDist > vecBlockBoundDist && exactDist != -1 && eBoundDist > exactDist)){
                            denyPassthoughEntity(targeter, errorPrefix);
                            return null;
                        }
                    }
                    return e;
                }
            }
        }
        if (errorPrefix != null){
            targeter.sendMessage(errorPrefix.append(MiniMessage.miniMessage().deserialize("<red>Target an entity within<yellow> "+distance+" <red>blocks of your location")));
            targeter.playSound(targeter, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
        }
        return null;
    }

    //Get Targeted Player w/o Message
    public static Entity getTargetEntity(Player targeter, double distance, PassthroughType passthroughType, boolean ignoreDead, EntityType... ignoredEntities) {
        return getTargetEntity(targeter, distance, passthroughType, null, ignoreDead, ignoredEntities);
    }

    public static boolean passesPassthroughCheck(Location location, PassthroughType passthroughType) {
        return passesPassthroughCheck(location, passthroughType, null, null);
    }

    public static boolean passesPassthroughCheck(Location location, PassthroughType passthroughType, Player player, Component erroPrefix) {
        Block block = location.getBlock();
        Material material = block.getType();
        switch (passthroughType){
            //Air
            case AIR_ONLY -> {
                if (!(block.getType() == Material.AIR || material == Material.VOID_AIR || material == Material.CAVE_AIR)){
                    denyPassthoughEntity(player, erroPrefix);
                    return false;
                }
            }
            //Passables
            case PASSABLE_ONLY, PASSABLE_AND_WATER, PASSABLE_AND_FLUIDS, PASSABLE_AND_LAVA -> {
                //Lava Checks
                if (material == Material.LAVA){
                    if (!(passthroughType == PassthroughType.PASSABLE_AND_FLUIDS || passthroughType == PassthroughType.PASSABLE_AND_LAVA)){
                        denyPassthoughEntity(player, erroPrefix);
                        return false;
                    }
                }
                //Water Checks
                else if (material == Material.WATER){
                    if (!(passthroughType == PassthroughType.PASSABLE_AND_FLUIDS || passthroughType == PassthroughType.PASSABLE_AND_WATER)){
                        denyPassthoughEntity(player, erroPrefix);
                        return false;
                    }
                }

                //Semi-Passable Block Checks
                if (!block.isPassable()){
                    //Gate Checks
                    if (ItemUtils.isGate(material) && !(((Gate) block.getBlockData()).isOpen())) {
                        if (!passesBoundingBoxCheck(block, location)){
                            denyPassthoughEntity(player, erroPrefix);
                            return false;
                        }

                    }
                    //Other "Non-Passable"
                    else {
                        if (!passesBoundingBoxCheck(block, location)){
                            denyPassthoughEntity(player, erroPrefix);
                            return false;
                        }
                        /*
                        //Bukkit.broadcast(Component.text("SEMI-PASSABLE | "+material.name()));
                        //Player can be standing in front of a closed door/trapdoor and still get detected
                        if (block.getBlockData() instanceof Openable && material != Material.BARREL){
                            if (!passesBoundingBoxCheck(block, location)){
                                denyPassthoughEntity(player, prefix);
                                return false;
                            }
                            checkOpenables = true;
                        }
                        else{
                            denyPassthoughEntity(player, prefix);
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
                            denyPassthoughEntity(player, erroPrefix);
                            return false;
                        }
                    }
                }
                //Water Checks
                else if (material == Material.WATER){
                    if (!(passthroughType == PassthroughType.ALL_BUT_FLUIDS || passthroughType == PassthroughType.ALL_BUT_WATER)){
                        if (block.getBoundingBox().contains(location.toVector())){
                            //Bukkit.broadcast(Component.text("DENIED VECTOR BOUNDS WATER"));
                            denyPassthoughEntity(player, erroPrefix);
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


    private static void denyPassthough(Player p, Component errorPrefix){
        if (p == null || errorPrefix == null){
            return;
        }
        p.sendMessage(errorPrefix.append(Component.text("You cannot target a player through that block with this!", NamedTextColor.RED)));
        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
    }

    private static void denyPassthoughEntity(Player p, Component errorPrefix){
        if (p == null || errorPrefix == null){
            return;
        }
        p.sendMessage(errorPrefix.append(Component.text("You cannot target a entity through that block with this!", NamedTextColor.RED)));
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
