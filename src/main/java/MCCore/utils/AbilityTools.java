package MCCore.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

public class AbilityTools {

//Nearby Players at Player
    public static List<Player> getNearbyPlayers(Player p, double d, boolean sendMSG, String prefix){
        Collection<Entity> coll = p.getNearbyEntities(d, d, d);
        if (p.getNearbyEntities(d,d,d).stream().noneMatch(en->en instanceof Player)) {
            if (sendMSG){
                p.sendMessage(prefix+ChatColor.RED+"You are not within "+d+" blocks of any players!");
                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
            }
            return null;
        }
        Entity[] toPlayer = coll.toArray(new Entity[coll.size()]);
        List<Player> players = new ArrayList<>();
        for(Entity e : toPlayer){
            if (e instanceof Player && e != p && !(e.hasMetadata("NPC"))){
                players.add((Player) e);
            }
        }
        if (players.size() > 0){
            return players;
        }
        return null;
    }

//Nearby Players at Location
    public static List<Player> getNearbyPlayers(Player p, double d, Location l, boolean sendMSG, String prefix){
        Collection<Entity> coll = p.getWorld().getNearbyEntities(l, d, d, d);
        if (p.getWorld().getNearbyEntities(l, d,d,d).stream().noneMatch(en->en instanceof Player)) {
            if (sendMSG){
                p.sendMessage(prefix+ChatColor.RED+"You are not within "+d+" blocks of any players!");
                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
            }
            return null;
        }
        Entity[] toPlayer = coll.toArray(new Entity[coll.size()]);
        List<Player> players = new ArrayList<>();
        for(Entity e : toPlayer){
            if (e instanceof Player && e != p && !(e.hasMetadata("NPC"))){
                players.add((Player) e);
            }
        }
        if (players.size() > 0){
            return players;
        }
        return null;
    }

//Get Targeted Player
    public static Player returnTargetPlayer(Player p, double distance, boolean allowThorughBlocks, String prefix) {
        Location loc = p.getEyeLocation();
        Vector v = p.getEyeLocation().getDirection();
        float t = 0;
        for(int i = 0; i<distance*2; i++) {
            t += 0.5f;
            double x = v.getX() * t;
            double y = v.getY() * t;
            double z = v.getZ() * t;
            Location locClone = loc.clone().add(x,y,z);
            if (!allowThorughBlocks){
                if (locClone.getBlock().getType().name().contains("DOOR")){
                    Openable door = (Openable) locClone.getBlock().getBlockData();
                    if (!door.isOpen()){
                        denyPassthough(p, prefix);
                        return null;
                    }
                }
                else if (!locClone.getBlock().isPassable()){ //Cant use through blocks and If block is full
                    denyPassthough(p, prefix);
                    return null;
                }
            }


            Collection<Entity> nearby = locClone.getWorld().getNearbyEntities(locClone, 0.35, 0.35, 0.35);
            //loc.subtract(x,y,z); //Resets loc instead of adding again from the location further than starting point
            if (!nearby.isEmpty()){
                Entity[] arr = nearby.toArray(new Entity[nearby.size()]);
                for (Entity e : arr) {
                    if (e instanceof Player && (e != p && !(e.hasMetadata("NPC")))) {//Not the player and not an NPC
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
        p.sendMessage(prefix+ChatColor.RED+"You cannot target a player through blocks with this ability!");
        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
    }
}
