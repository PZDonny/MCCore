package MCCore.listeners;

import MCCore.Core;
import MCCore.minigameAPI.GameState;
import MCCore.minigameAPI.arenaManager.Arena;
import MCCore.minigameAPI.arenaManager.ArenaManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class Teleport implements Listener {

    @EventHandler
    public void onTP(PlayerTeleportEvent e){
        if (!Core.isMinigameEnabled()){
            return;
        }

        Player p = e.getPlayer();
        Arena arena = ArenaManager.getArenaOfPlayer(p);
        if (arena == null || arena.isEndingOrNotUsable()){
            return;
        }

        if (e.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE){
            e.setCancelled(true);
        }
    }
}