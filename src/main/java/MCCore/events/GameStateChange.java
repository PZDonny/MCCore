package MCCore.events;

import MCCore.minigameAPI.GameState;
import MCCore.minigameAPI.arenaManager.Arena;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class GameStateChange extends Event {

    private static final HandlerList handlers = new HandlerList();

    Arena arena;
    GameState gameState;

    public GameStateChange(Arena arena, GameState gameState){
        this.arena = arena;
        this.gameState = gameState;
    }

    public GameState getGameState(){
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
