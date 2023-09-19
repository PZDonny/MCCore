package MCCore.events;

import MCCore.minigameAPI.arenaManager.Arena;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArenaWorldGeneratedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    Arena arena;

    public ArenaWorldGeneratedEvent(Arena arena){
        this.arena = arena;
    }

    public Arena getArena(){
        return arena;
    }



    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
