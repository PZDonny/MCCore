package MCCore.utils;

import MCCore.Core;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
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
        if (bukkitWorld == null) return;
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
}
