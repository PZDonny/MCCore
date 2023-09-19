package MCCore.events;

import MCCore.utils.AbilityHandler;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class AbilityReturnedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    KeyedBossBar bar;
    Player player;
    AbilityHandler.CooldownType cooldownType;

    public AbilityReturnedEvent(KeyedBossBar bar, Player player, AbilityHandler.CooldownType type){
        this.bar = bar;
        this.player = player;
        this.cooldownType = type;
    }

    public BossBar getBar(){
        return bar;
    }

    public String getBarID(){
        return bar.getKey().getKey();
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
