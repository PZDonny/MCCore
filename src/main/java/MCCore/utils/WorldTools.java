package MCCore.utils;

import MCCore.Core;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class WorldTools {
    public static boolean isLowestBlock(Location loc1) {
        Location loc2 = loc1.clone();
        loc2.setY(-64);
        List<Block> blocks = new ArrayList<>();

        int topBlockX = (Math.max(loc1.getBlockX(), loc2.getBlockX()));
        int bottomBlockX = (Math.min(loc1.getBlockX(), loc2.getBlockX()));

        int topBlockY = (Math.max(loc1.getBlockY(), loc2.getBlockY()));
        int bottomBlockY = (Math.min(loc1.getBlockY(), loc2.getBlockY()));

        int topBlockZ = (Math.max(loc1.getBlockZ(), loc2.getBlockZ()));
        int bottomBlockZ = (Math.min(loc1.getBlockZ(), loc2.getBlockZ()));

        for(int x = bottomBlockX; x <= topBlockX; x++)
        {
            for(int z = bottomBlockZ; z <= topBlockZ; z++)
            {
                for(int y = bottomBlockY; y <= topBlockY; y++)
                {
                    Block block = loc1.getWorld().getBlockAt(x, y, z);

                    blocks.add(block);
                }
            }
        }

        for (Block b : blocks){
            if (!b.getType().equals(Material.AIR)) return false;
        }
        return true;
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
