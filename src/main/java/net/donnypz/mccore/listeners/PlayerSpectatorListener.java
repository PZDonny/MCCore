package net.donnypz.mccore.listeners;

import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import net.donnypz.mccore.Core;
import net.donnypz.mccore.minigame.arena.Arena;
import net.donnypz.mccore.minigame.arena.ArenaManager;
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.DecoratedPot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.loot.Lootable;

public class PlayerSpectatorListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFlowerPot(PlayerInteractEvent e){
        Player p = e.getPlayer();
        //Spectators
        if (Core.isMinigameEnabled()){
            Arena arena = ArenaManager.getArenaOfPlayer(p);
            if (arena != null && arena.isPlayerSpectating(p, false)){
                if (arena.isSpectatorInteractionAllowed()){
                    return;
                }
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFoodLevelChange(FoodLevelChangeEvent e){
        if (!(e.getEntity() instanceof Player p)){
            return;
        }
        Arena arena = ArenaManager.getArenaOfPlayer(p);
        if (arena != null && arena.isPlayerSpectating(p, false)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
    //Spectators
        if (Core.isMinigameEnabled()){
            Arena arena = ArenaManager.getArenaOfPlayer(p);
            if (arena == null){
                return;
            }

            if (arena.isSpectatorInteractionAllowed()){
                return;
            }

            if (arena.isPlayerSpectating(p, false)){
                if (e.getAction() == Action.PHYSICAL){ //Interacting with environment
                    e.setCancelled(true);
                }

                else if (e.getAction() == Action.RIGHT_CLICK_BLOCK){ //Opening Doors / Containers
                    Block block = e.getClickedBlock();
                    if (block == null){
                        return;
                    }

                    if (block.getBlockData() instanceof Openable
                            || block.getBlockData() instanceof DecoratedPot
                            || block instanceof Lootable
                            || block instanceof BlockInventoryHolder){
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemFrameChange(PlayerItemFrameChangeEvent e){
        Player p = e.getPlayer();
        //Spectators
        if (!Core.isMinigameEnabled()){
            return;
        }
        Arena arena = ArenaManager.getArenaOfPlayer(p);
        if (arena == null){
            return;
        }

        if (arena.isSpectatorInteractionAllowed()){
            return;
        }

        if (arena.isPlayerSpectating(p, false)){
            e.setCancelled(true);
        }
    }

//Spectator Arrow Collision
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onProjectileCollide(ProjectileHitEvent e){
        if (!Core.isMinigameEnabled()){
            return;
        }

        if (!(e.getHitEntity() instanceof Player p)){
            return;
        }

        Arena arena = ArenaManager.getArenaOfPlayer(p);
        if (arena == null){
            return;
        }

        if (arena.isPlayerSpectating(p, false)){
            e.setCancelled(true);
        }
    }


}
