package MCCore.events;

import MCCore.minigameAPI.arenaManager.Arena;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

public class PlayerRemovedFromArenaEvent extends Event {

    public enum RemoveCause{
        JOINEDNEW,
        DISCONNECT,
    }

    private static final HandlerList handlers = new HandlerList();

    Arena arena;
    Player player;

    RemoveCause cause;
    List<Entity> dismountedPassengers;
    Entity oldVehicle;

    public PlayerRemovedFromArenaEvent(Arena arena, Player player, RemoveCause cause){
        this.arena = arena;
        this.player = player;
        this.cause = cause;
        if (player.isOnline()){
            this.dismountedPassengers = new ArrayList<>(player.getPassengers());
            for (Entity passenger : dismountedPassengers){
                player.removePassenger(passenger);
            }

            this.oldVehicle = player.getVehicle();
            player.leaveVehicle();
        }
    }

    public PlayerRemovedFromArenaEvent(Arena arena, Player player, RemoveCause cause, List<Entity> dismountedPassengers, Entity oldVehicle){
        this.arena = arena;
        this.player = player;
        this.cause = cause;
        this.dismountedPassengers = dismountedPassengers;
        this.oldVehicle = oldVehicle;
    }

    public Arena getArena(){
        return arena;
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

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
