package net.donnypz.mccore.utils;

import net.donnypz.mccore.Core;
import net.donnypz.mccore.utils.item.ItemUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Stack;

public class WorldUtils {
    public static boolean isLowestBlock(Location location) {
        Stack<Block> blocks = new Stack<>();
        int x = location.getBlockX();
        int z = location.getBlockZ();

        for(int y = location.getBlockY(); y >= -64; y--){
            Block block = location.getWorld().getBlockAt(x, y, z);
            blocks.push(block);
        }

        Block lowestfullBlock = null;
        for (int i = 0; i <= blocks.size(); i++){
            Block b = blocks.pop();
            if (!ItemUtils.isAir(b.getType()) && !b.isPassable()){
                if (b.getY() < location.getBlockY()){
                    return false;
                }
                lowestfullBlock = b;
            }
        }
        return location.getBlock().equals(lowestfullBlock);
    }

    public static Block getHighestBlockUnderLocation(Location location){
        int x = location.getBlockX();
        int z = location.getBlockZ();
        for (int y = location.getBlockY(); y >= -64; y--){
            Block block = location.getWorld().getBlockAt(x, y, z);
            if (!ItemUtils.isAir(block.getType()) && !block.isPassable()){
                return block;
            }
        }
        return null;
    }

    public static void destroyWorld(World bukkitWorld) {
        if (bukkitWorld == null){
            return;
        }
        Location defaultWorld = Bukkit.getWorlds().getFirst().getSpawnLocation();
        for (Player p : bukkitWorld.getPlayers()){
            p.teleport(defaultWorld);
        }

        new BukkitRunnable(){
            @Override
            public void run() {
                bukkitWorld.setAutoSave(false);

                for (Entity e : bukkitWorld.getEntities()){
                    e.remove();
                }

                for (Chunk chunk : bukkitWorld.getLoadedChunks()) {
                    chunk.unload();
                }

                Bukkit.unloadWorld(bukkitWorld, true);
            }
        }.runTaskLater(Core.getInstance(), 5);
    }
    
    public static void useMinigameGamerules(World world){
        world.setGameRule(GameRule.DISABLE_RAIDS, true);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setGameRule(GameRule.DO_INSOMNIA, false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.MOB_GRIEFING, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
        world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
        world.setGameRule(GameRule.SPAWN_CHUNK_RADIUS, 0);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.setGameRule(GameRule.FORGIVE_DEAD_PLAYERS, true);
        world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
        world.setGameRule(GameRule.ENDER_PEARLS_VANISH_ON_DEATH, true);
        world.setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE, 101);
        world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
        world.setDifficulty(Difficulty.NORMAL);
    }
    
}
