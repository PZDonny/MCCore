package MCCore.listeners;

import MCCore.Core;
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
        if (newWorld.equals(Core.getInstance().getMinigameWaitingWorld())){
            ArenaManager.removePlayerFromArena(p);
        }
    }
}
