package MCCore.events;

import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class TimedBarEndEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    KeyedBossBar bar;
    List<Player> players;

    public TimedBarEndEvent(KeyedBossBar bar, List<Player> players){
        this.bar = bar;
        this.players = players;
    }

    public BossBar getBar(){
        return bar;
    }

    public String getBarID(){
        return bar.getKey().getKey();
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
