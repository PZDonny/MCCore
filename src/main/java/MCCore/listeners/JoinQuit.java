package MCCore.listeners;

import MCCore.Core;
import MCCore.minigameAPI.arenaManager.ArenaManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuit implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        Chat.playerChatChannels.put(p.getUniqueId(), 0);
        ArenaManager.addPlayerToHashArena(p);
        if (Core.isMinigameEnabled()) p.teleport(Core.getInstance().getMinigameWaitingWorld().getSpawnLocation());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        Chat.playerChatChannels.remove(p.getUniqueId());
        ArenaManager.removePlayerFromArena(p, true);
    }
}
