package MCCore.events;

import MCCore.minigameAPI.arenaManager.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerRemovedFromArenaEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    Arena arena;
    Player player;

    public PlayerRemovedFromArenaEvent(Arena arena, Player player){
        this.arena = arena;
    }

    public Arena getArena(){
        return arena;
    }

    public Player getPlayer(){
        return player;
    }



    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
