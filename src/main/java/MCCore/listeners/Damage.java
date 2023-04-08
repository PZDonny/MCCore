package MCCore.listeners;

import MCCore.Core;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;


public class Damage implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent e){
        Entity entity = e.getEntity();
        if (Core.getInstance().getMinigameWaitingWorld() != null){
            if (entity instanceof Player){
                Player p = (Player) entity;
                if (p.getWorld().equals(Core.getInstance().getMinigameWaitingWorld())) e.setCancelled(true);
            }

        }


    }
}
