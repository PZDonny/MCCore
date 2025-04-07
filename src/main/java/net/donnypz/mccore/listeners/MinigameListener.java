package net.donnypz.mccore.listeners;

import net.donnypz.mccore.Core;
import net.donnypz.mccore.events.PlayerRemovedFromArenaEvent;
import net.donnypz.mccore.minigame.arenaManager.Arena;
import net.donnypz.mccore.minigame.arenaManager.ArenaManager;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;

public class MinigameListener implements Listener {

//Prevent Item Drops
    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (!Core.isMinigameEnabled()) {
            return;
        }

        Player p = e.getPlayer();
        if (p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR){
            return;
        }

    //Arena Related
        Arena arena = ArenaManager.getArenaOfPlayer(p);
        if (arena != null){
            if (arena.isPlayerSpectating(p, false)){
                e.setCancelled(true);
                return;
            }
            if (Core.isWaitingWorldLimited() && p.getWorld().equals(Core.getInstance().getMinigameWaitingWorld())){
                e.setCancelled(true);
            }
        }
    }

//World Changes (Arena Removal)
    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e){
        Player p = e.getPlayer();
        World oldWorld = e.getFrom();
        Arena arena = ArenaManager.getArena(oldWorld.getName());
        if (arena == null || arena.isEndingOrNotUsable() || arena.allowsMultipleWorlds()){
            return;
        }

        if (arena.getArenaPlayers().contains(p)){
            /*List<Entity> passengers = new ArrayList<>(p.getPassengers());
            for (Entity passenger : passengers){
                p.removePassenger(passenger);
            }
            Entity vehicle = p.getVehicle();
            p.leaveVehicle();
            ArenaContainer container = ArenaContainer.getArenaContainer(arena);*/
            if (p.getWorld() == Core.getInstance().getMinigameWaitingWorld() && Core.isWaitingWorldLimited()){
                arena.removePlayer(p, PlayerRemovedFromArenaEvent.RemoveCause.JOINEDNEW);
                //new PlayerRemovedFromArenaEvent(arena, container, p, PlayerRemovedFromArenaEvent.RemoveCause.JOINEDNEW, passengers, vehicle).callEvent();
            }
            else if (!Core.isWaitingWorldLimited()){
                arena.removePlayer(p, PlayerRemovedFromArenaEvent.RemoveCause.MANUALARENA);
                //new PlayerRemovedFromArenaEvent(arena, container, p, PlayerRemovedFromArenaEvent.RemoveCause.MANUALARENA, passengers, vehicle).callEvent();
            }
            else{
                arena.removePlayer(p, PlayerRemovedFromArenaEvent.RemoveCause.UNKNOWN);
                //new PlayerRemovedFromArenaEvent(arena, container, p, PlayerRemovedFromArenaEvent.RemoveCause.UNKNOWN, passengers, vehicle).callEvent();
            }
        }
    }

//Player Dismount BlockDisplay (Arena Waiting World)
    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e){
        if (!Core.isMinigameEnabled() || !Core.isWaitingWorldLimited()){
            return;
        }

        Player p = e.getPlayer();
        if (p.getGameMode() != GameMode.SPECTATOR){
            return;
        }

        if (!p.getWorld().equals(Core.getInstance().getMinigameWaitingWorld())){
            return;
        }

        if (ArenaManager.getArenaOfPlayer(p) == null){
            return;
        }

        e.setCancelled(true);
    }

//Player Teleporting (Waiting World Spectators)
    @EventHandler
    public void onTP(PlayerTeleportEvent e){
        if (!Core.isMinigameEnabled()){
            return;
        }

        Player p = e.getPlayer();
        Arena arena = ArenaManager.getArenaOfPlayer(p);
        if (arena == null || arena.isEndingOrNotUsable()){
            return;
        }

        if (e.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE){
            e.setCancelled(true);
        }
    }

//Player Damage
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent e){
        if (!(e.getEntity() instanceof Player p)){
            return;
        }

    //Waiting World Damage
        if (Core.isWaitingWorldLimited() && p.getWorld().equals(Core.getInstance().getMinigameWaitingWorld())){
            e.setCancelled(true);
        }
    }


//Player Arrow Pickup
    @EventHandler
    public void onArrowPickup(PlayerPickupArrowEvent e){
        if (!Core.isMinigameEnabled()){
            return;
        }

        Player p = e.getPlayer();
        if (Core.isWaitingWorldLimited() && p.getWorld().equals(Core.getInstance().getMinigameWaitingWorld())){
            if (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE){
                e.setCancelled(true);
            }
        }
        else{
            Arena arena = ArenaManager.getArenaOfPlayer(p);
            if (arena == null){
                return;
            }

            if (arena.isPlayerSpectating(p, false)){
                e.setCancelled(true);
            }
        }
    }
}
