package MCCore.listeners;

import MCCore.Core;
import MCCore.utils.AbilityHandler;
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
        if (entity instanceof Player p){
        //Minigame Waiting World
            if (Core.getInstance().getMinigameWaitingWorld() != null){
                if (p.getWorld().equals(Core.getInstance().getMinigameWaitingWorld())){
                    e.setCancelled(true);
                    return;
                }
            }
        //Ability Fall Damage Resistance
            if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL)){
                if (AbilityHandler.isPlayerFallDamageResistant(p)){
                    e.setCancelled(true);
                    return;
                }
            }
        }



    }
}
