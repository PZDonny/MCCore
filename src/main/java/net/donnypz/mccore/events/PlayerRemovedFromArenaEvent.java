package net.donnypz.mccore.events;

import net.donnypz.mccore.Core;
import net.donnypz.mccore.minigame.arena.Arena;
import net.donnypz.mccore.minigame.arena.ArenaContainer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerRemovedFromArenaEvent extends ArenaEvent {

    public enum RemoveCause{
        JOINEDNEW,
        DISCONNECT,
        MANUALARENA,
        UNKNOWN;
    }

    Player player;

    RemoveCause cause;
    List<Entity> dismountedPassengers;
    Entity oldVehicle;
    private final boolean wasArenaPlayer;
    private boolean removeStartingPlayer = false;

    public PlayerRemovedFromArenaEvent(Arena arena, ArenaContainer arenaContainer, Player player, RemoveCause cause){
        super(arena, arenaContainer);
        this.player = player;
        this.cause = cause;
        this.wasArenaPlayer = arena.getStartPlayers().contains(player);
        if (player.isOnline()){
            this.dismountedPassengers = new ArrayList<>(player.getPassengers());
            for (Entity passenger : dismountedPassengers){
                player.removePassenger(passenger);
            }

            this.oldVehicle = player.getVehicle();
            player.leaveVehicle();
        }
    }

    public PlayerRemovedFromArenaEvent(Arena arena, ArenaContainer arenaContainer, Player player, RemoveCause cause, List<Entity> dismountedPassengers, Entity oldVehicle){
        super(arena, arenaContainer);
        this.player = player;
        this.cause = cause;
        this.dismountedPassengers = dismountedPassengers;
        this.oldVehicle = oldVehicle;
        this.wasArenaPlayer = arena.getStartPlayers().contains(player);
    }

    public Player getPlayer(){
        return player;
    }

    public RemoveCause getCause(){
        return cause;
    }

    public List<Entity> getDismountedPassengers() {
        return dismountedPassengers;
    }

    public Entity getOldVehicle() {
        return oldVehicle;
    }

    public boolean wasArenaPlayer() {
        return wasArenaPlayer;
    }

    public void removeStartingPlayer(){
        removeStartingPlayer = true;
    }

    public boolean isRemoveStartingPlayer() {
        return removeStartingPlayer;
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
}
