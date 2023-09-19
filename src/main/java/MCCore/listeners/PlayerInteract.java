package MCCore.listeners;

import MCCore.Core;
import MCCore.minigameAPI.PlayMinigame;
import MCCore.minigameAPI.arenaManager.Arena;
import MCCore.minigameAPI.arenaManager.ArenaManager;
import MCCore.utils.PlayerBalancerAPI;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerInteract implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
    //Spectators
        if (Core.isMinigameEnabled()){
        //Interacting with environment
            if (e.getAction() == Action.PHYSICAL){
                Arena arena = ArenaManager.getArenaOfPlayer(p);
                if (arena == null) return;
                if (arena.isPlayerSpectating(p) && !arena.isSpectatorInteractionAllowed()){
                    e.setCancelled(true);
                    return;
                }
            }
        //Opening Doors/Containers
            else if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
                Arena arena = ArenaManager.getArenaOfPlayer(p);
                if (arena == null) return;
                if (!arena.isPlayerSpectating(p) || arena.isSpectatorInteractionAllowed()) return;

                Block block = e.getClickedBlock();
                if (block == null) return;
                if (block.getBlockData() instanceof Openable || block instanceof Container){
                    e.setCancelled(true);
                    return;
                }
            }
        }


    //Ignore Physical Interaction and (if offhand is the hand detected AND both main and offhand are equipped w/ items)
        if (e.getAction() == Action.PHYSICAL
                || (e.getHand() == EquipmentSlot.OFF_HAND && p.getInventory().getItemInMainHand().getType() != Material.AIR && p.getInventory().getItemInOffHand().getType() != Material.AIR)
                || (e.getHand() == EquipmentSlot.OFF_HAND && p.getInventory().getItemInMainHand().getType() == Material.AIR && p.getInventory().getItemInOffHand().getType() != Material.AIR && e.getAction() == Action.RIGHT_CLICK_BLOCK)
                || (e.getClickedBlock() != null && e.getClickedBlock().getType().isInteractable())) {
            return;
        }

        ItemStack main = p.getInventory().getItemInMainHand();
        ItemStack off = p.getInventory().getItemInOffHand();

    //Return To Lobby
        ItemStack tool = null;

    //Lobby Tool
        if (main.equals(Arena.leaveTool) || main.equals(Arena.requeueTool)) tool = main;

        else if (off.equals(Arena.leaveTool) || off.equals(Arena.requeueTool)) tool = off;

        if (tool != null) {
            if (tool.equals(Arena.leaveTool)){
                PlayerBalancerAPI.connectPlayerToFallback(p);
            }
            else{
                Arena arena = ArenaManager.getArenaOfPlayer(p);
                if (arena != null){
                    PlayMinigame.join(p, arena.getMinigameName(), arena.getMode(), true);
                }

            }

            return;
        }

    }
}
