package MCCore.events;

import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class TimedBossBarEndEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    BossBar bar;
    List<Player> players;
    String barID;

    public TimedBossBarEndEvent(BossBar bar, List<Player> players){
        this.bar = bar;
        this.players = players;
        if (bar instanceof KeyedBossBar){
            barID = ((KeyedBossBar) bar).getKey().getKey();
        }
    }

    public BossBar getBar(){
        return bar;
    }

    public boolean hasBarID(){
        return bar instanceof KeyedBossBar;
    }

    public String getBarID(){
        return barID;
    }
    public List<Player> getPlayers(){
        return players;
    }



    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
