package MCCore.listeners;

import MCCore.events.PlayerRemovedFromArenaEvent;
import MCCore.minigameAPI.arenaManager.Arena;
import MCCore.minigameAPI.arenaManager.ArenaManager;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.util.ArrayList;
import java.util.List;

public class WorldChange implements Listener {

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e){
        Player p = e.getPlayer();
        World oldWorld = e.getFrom();
        Arena arena = ArenaManager.getArena(oldWorld.getName());
        if (arena != null && !arena.isEndingOrNotUsable()){
            List<Entity> passengers = new ArrayList<>(p.getPassengers());
            for (Entity passenger : passengers){
                p.removePassenger(passenger);
            }
            Entity vehicle = p.getVehicle();
            p.leaveVehicle();
            new PlayerRemovedFromArenaEvent(arena, p, PlayerRemovedFromArenaEvent.RemoveCause.JOINEDNEW, passengers, vehicle).callEvent();
        }
    }
}
