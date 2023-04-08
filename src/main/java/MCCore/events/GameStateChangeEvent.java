package MCCore.events;

import MCCore.minigameAPI.GameState;
import MCCore.minigameAPI.arenaManager.Arena;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class GameStateChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    Arena arena;
    GameState gameState;

    GameState pastGameState;

    public GameStateChangeEvent(Arena arena, GameState gameState, GameState pastGameState){
        this.arena = arena;
        this.gameState = gameState;
        this.pastGameState = pastGameState;
    }

    public GameState getGameState(){
        return gameState;
    }

    public GameState getPastGameState(){
        return gameState;
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
