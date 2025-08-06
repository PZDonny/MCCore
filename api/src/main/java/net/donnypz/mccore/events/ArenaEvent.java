package net.donnypz.mccore.events;

import net.donnypz.mccore.minigame.arena.Arena;
import net.donnypz.mccore.minigame.arena.ArenaContainer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

abstract class ArenaEvent extends Event {


    private static final HandlerList handlers = new HandlerList();
    protected final Arena arena;
    private final ArenaContainer arenaContainer;

    ArenaEvent(Arena arena){
        this.arena = arena;
        this.arenaContainer = ArenaContainer.getArenaContainer(arena);
    }

    ArenaEvent(Arena arena, ArenaContainer arenaContainer){
        this.arena = arena;
        this.arenaContainer = arenaContainer;
    }

    public Arena getArena(){
        return arena;
    }

    public ArenaContainer getArenaContainer(){
        return arenaContainer;
    }

    public <T> T getArenaContainer(Class<T> arenaContainerClass){
        return arenaContainerClass.cast(arenaContainer);
    }

    public boolean hasArenaContainer(){
        return arenaContainer != null;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
