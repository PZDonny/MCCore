package MCCore.utils;

import MCCore.Core;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Stack;

public class WorldTools {
    public static boolean isLowestBlock(Location loc1) {
        Stack<Block> blocks = new Stack<>();
        int x = loc1.getBlockX();
        int z = loc1.getBlockZ();

        for(int y = loc1.getBlockY(); y >= -64; y--){
            Block block = loc1.getWorld().getBlockAt(x, y, z);
            blocks.push(block);
        }

        Block lowestfullBlock = null;
        for (int i = 0; i <= blocks.size(); i++){
            Block b = blocks.pop();
            if (!Items.isAir(b.getType()) && !b.isPassable()){
                if (b.getY() < loc1.getBlockY()){
                    return false;
                }
                lowestfullBlock = b;
            }
        }
        return loc1.getBlock().equals(lowestfullBlock);
    }

    public static void destroyWorld(World bukkitWorld) {
        if (bukkitWorld == null){
            return;
        }
        Location defaultWorld = Bukkit.getWorlds().get(0).getSpawnLocation();
        for (Player p : bukkitWorld.getPlayers()){
            p.teleport(defaultWorld);
        }

        new BukkitRunnable(){
            @Override
            public void run() {
                bukkitWorld.setKeepSpawnInMemory(false);
                bukkitWorld.setAutoSave(false);

                for (Entity e : bukkitWorld.getEntities()){

                    e.remove();
                }
                for (Chunk chunk : bukkitWorld.getLoadedChunks()) {
                    chunk.unload(false);
                }
                Bukkit.unloadWorld(bukkitWorld, true);
            }
        }.runTaskLater(Core.getInstance(), 5);
    }
    
    public static void setGamerulesToMinigame(World world){
        world.setGameRule(GameRule.DISABLE_RAIDS, true);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setGameRule(GameRule.DO_INSOMNIA, false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.MOB_GRIEFING, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
        world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
        world.setGameRule(GameRule.SPAWN_RADIUS, 0);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.setGameRule(GameRule.FORGIVE_DEAD_PLAYERS, true);
        world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
        world.setDifficulty(Difficulty.NORMAL);
    }
    
}
