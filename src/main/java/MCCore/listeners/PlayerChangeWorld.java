package MCCore.listeners;

import MCCore.minigameAPI.arenaManager.Arena;
import MCCore.minigameAPI.arenaManager.ArenaManager;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PlayerChangeWorld implements Listener {

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e){

        Player p = e.getPlayer();
        World pastWorld = e.getFrom();
        World newWorld = p.getWorld();
        Arena arena = ArenaManager.getArenaOfPlayer(p);
        /*if (arena != null && pastWorld.getName().equals(arena.getArenaWorld().getName())){
            Bukkit.getPluginManager().callEvent(new PlayerExitArenaEvent(arena, p));
        }*/

        /*if (newWorld.equals(Core.getInstance().getMinigameWaitingWorld())){
            ArenaManager.removePlayerFromArena(p, true);
        }*/
    }
}
