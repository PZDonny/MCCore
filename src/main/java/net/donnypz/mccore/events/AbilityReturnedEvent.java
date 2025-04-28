package net.donnypz.mccore.events;

import net.donnypz.mccore.utils.ability.AbilityHandler;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class AbilityReturnedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    KeyedBossBar bar;
    String barID;
    Player player;
    AbilityHandler.CooldownType cooldownType;

    public AbilityReturnedEvent(KeyedBossBar bar, String barID, Player player, AbilityHandler.CooldownType type){
        this.bar = bar;
        this.barID = barID;
        this.player = player;
        this.cooldownType = type;
    }

    public KeyedBossBar getBar(){
        return bar;
    }

    public String getBarID(){
        return barID;
    }
    public Player getPlayer(){
        return player;
    }

    public AbilityHandler.CooldownType getCooldownType() {
        return cooldownType;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
