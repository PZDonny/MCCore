package net.donnypz.mccore.listeners;

import net.donnypz.mccore.utils.ability.AbilityHandler;
import net.donnypz.mccore.utils.particles.FireworkUtils;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void damage(EntityDamageEvent e){
        if (!(e.getEntity() instanceof Player p)){
            return;
        }

    //Fall Damage Resistance
        if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL)){
            if (AbilityHandler.isFallDamageResistant(p)){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void damageByEntity(EntityDamageByEntityEvent e){
        if (e.getDamager() instanceof Firework fw){
            if (FireworkUtils.isDamageDisabled(fw)){
                e.setCancelled(true);
            }
        }
    }
}
