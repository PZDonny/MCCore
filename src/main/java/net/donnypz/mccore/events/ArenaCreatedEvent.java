package net.donnypz.mccore.events;

import net.donnypz.mccore.minigame.arenaManager.Arena;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class ArenaCreatedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    Arena arena;

    public ArenaCreatedEvent(Arena arena){
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
