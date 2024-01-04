package MCCore.listeners;

import MCCore.minigameAPI.arenaManager.Arena;
import MCCore.minigameAPI.arenaManager.ArenaManager;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkUnload implements Listener {

    @EventHandler
    public void onUnload(ChunkUnloadEvent e){
        World w = e.getWorld();
        Arena arena = ArenaManager.getArena(w.getName());
        if (arena != null && !arena.isUsable()){
            e.setSaveChunk(false);
        }
    }
}
