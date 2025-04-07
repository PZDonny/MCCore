package net.donnypz.mccore.events;

import net.donnypz.mccore.minigame.GameState;
import net.donnypz.mccore.minigame.arenaManager.Arena;


public class GameStateChangeEvent extends ArenaEvent {

    GameState gameState;
    GameState pastGameState;

    public GameStateChangeEvent(Arena arena, GameState gameState, GameState pastGameState){
        super(arena);
        this.gameState = gameState;
        this.pastGameState = pastGameState;
    }

    public GameState getGameState(){
        return gameState;
    }

    public GameState getPastGameState(){
        return gameState;
    }

}
