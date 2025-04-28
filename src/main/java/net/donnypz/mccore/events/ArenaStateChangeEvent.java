package net.donnypz.mccore.events;

import net.donnypz.mccore.minigame.ArenaState;
import net.donnypz.mccore.minigame.arena.Arena;


public class ArenaStateChangeEvent extends ArenaEvent {

    ArenaState arenaState;
    ArenaState pastArenaState;

    public ArenaStateChangeEvent(Arena arena, ArenaState arenaState, ArenaState pastArenaState){
        super(arena);
        this.arenaState = arenaState;
        this.pastArenaState = pastArenaState;
    }

    public ArenaState getState(){
        return arenaState;
    }

    public ArenaState getPastState(){
        return arenaState;
    }

}
