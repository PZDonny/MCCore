package net.donnypz.mccore.utils.misc;

import net.donnypz.mccore.utils.item.ItemUtils;
import net.donnypz.mccore.version.CoreAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Stack;

public class WorldUtils {
    public static boolean isLowestBlock(@NotNull Location location) {
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

    public static Block getHighestBlockUnderLocation(@NotNull Location location){
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

    public static void destroyWorld(@NotNull World bukkitWorld) {
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
        }.runTaskLater(CoreAPI.getPlugin(), 5);
    }
    
    public static void useMinigameGamerules(@NotNull World world){
        CoreAPI.getVersionHandler().useMinigameGamerules(world);
        world.setDifficulty(Difficulty.NORMAL);
    }
    
}
