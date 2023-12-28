package MCCore.listeners;

import MCCore.Core;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class Sneak implements Listener {
    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e){
        if (!Core.isMinigameEnabled()){
            return;
        }
        Player p = e.getPlayer();
        if (p.getGameMode() != GameMode.SPECTATOR){
            return;
        }
        if (!p.getWorld().equals(Core.getInstance().getMinigameWaitingWorld())){
            return;
        }

        e.setCancelled(true);

    }
}
