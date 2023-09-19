package MCCore.listeners;

import MCCore.Core;
import MCCore.minigameAPI.arenaManager.Arena;
import MCCore.minigameAPI.arenaManager.ArenaManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupArrowEvent;

public class ArrowPickup implements Listener {

    @EventHandler
    public void onArrowPickup(PlayerPickupArrowEvent e){
        Player p = e.getPlayer();
        if (p.getWorld().equals(Core.getInstance().getMinigameWaitingWorld()) && (p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE))){
            e.setCancelled(true);
        }

        Arena arena = ArenaManager.getArenaOfPlayer(p);
        if (arena == null) return;

        if (arena.isPlayerSpectating(p)){
            e.setCancelled(true);
        }

    }
}
