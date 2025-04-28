package net.donnypz.mccore.events;

import net.donnypz.mccore.Core;
import net.donnypz.mccore.minigame.arena.Arena;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerAddedToArenaEvent extends ArenaEvent {

    Player player;
    boolean addedAsPlayingPlayer = false;
    AddType addType;

    public PlayerAddedToArenaEvent(Arena arena, Player player, AddType addType){
        super(arena);
        this.player = player;
        this.addType = addType;
    }


    public Player getPlayer(){
        return player;
    }

    public AddType getType(){
        return addType;
    }


    public void addPlayingPlayer(boolean addStartingPlayer){
        arena.addPlayingPlayer(player, addStartingPlayer);
    }

    public void addSpectatingPlayer(){
        arena.makePlayerSpectate(player, true, true);
    }

    public boolean isAddedAsPlayingPlayer(){
        return addedAsPlayingPlayer;
    }

    public void revealPlayersToPlayer(List<Player> players){
        for (Player p : players){
            player.showPlayer(Core.getInstance(), p);
        }
    }

    public void revealPlayerToPlayers(List<Player> players){
        for (Player p : players){
            p.showPlayer(Core.getInstance(), player);
        }
    }

    public enum AddType{
        MANUAL,
        AUTOMATIC;
    }
}
