package net.donnypz.mccore.utils.item;

import net.donnypz.mccore.minigame.arena.ArenaManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class Listener_ItemClick implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent e){
        if (ItemAction.hasItemAction(e.getItemInHand())){
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.PHYSICAL){
            return;
        }
        Player p = e.getPlayer();
        Block b = e.getClickedBlock();
        boolean mainHasAction = ItemUtils.hasItemAction(p.getInventory().getItemInMainHand());
        boolean offHasAction = ItemUtils.hasItemAction(p.getInventory().getItemInOffHand());

        if (//|| (e.getHand() == EquipmentSlot.OFF_HAND && p.getInventory().getItemInMainHand().getType() != Material.AIR && p.getInventory().getItemInOffHand().getType() != Material.AIR)
                (b != null
                && b.getType().isInteractable() && !ItemUtils.isStairs(b.getType()))) {
            return;
        }

    //Hand Detected is Offhand and Main has nothing
        if (e.getHand() == EquipmentSlot.OFF_HAND){
            if (p.getInventory().getItemInMainHand().getType() == Material.AIR && e.getAction() == Action.RIGHT_CLICK_BLOCK){
                //Disable Block Placement
                if (p.getInventory().getItemInOffHand().getType().isBlock() && (offHasAction || mainHasAction)){
                    e.setCancelled(true);
                }
            }

        //Main Hand has Item Action (Overrides Offhand)
            if (mainHasAction) {
                return;
            }
        }

    //Hand Detected is Main and Offhand has nothing
        if (e.getHand() == EquipmentSlot.HAND){

        //Disable Block Placement
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK && mainHasAction){
                if (p.getInventory().getItemInMainHand().getType().isBlock()){
                    e.setCancelled(true);
                }

                else if (!ItemUtils.isAir(p.getInventory().getItemInOffHand().getType()) && p.getInventory().getItemInOffHand().getType().isBlock()){
                    e.setCancelled(true);
                    return;
                }
            }
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR){
                if (offHasAction && !mainHasAction){
                    return;
                }
            }
        }

        ItemStack main = p.getInventory().getItemInMainHand();
        ItemStack off = p.getInventory().getItemInOffHand();
        
        //Abilities
        ItemStack i = null;

        if (offHasAction) {
            i = off;
        }

        if (mainHasAction) {
            i = main;
        }

        if (i == null || ItemUtils.isAir(i.getType())) {
            return;
        }

        String id = ItemUtils.getPDCKey(i, ItemUtils.itemActionKey, PersistentDataType.STRING);
        ItemAction itemAction = ItemAction.getItemAction(id);
        if (itemAction == null){
            return;
        }


//Cast the Abilities-----------------------------------------------------------------------------------------

        if (main == i){
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR){
                if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
                    if (off.getType() == Material.BRUSH){
                        return;
                    }
                }
                if (isBlockingItem(off)){
                    return;
                }
            }
        }


        if (off == i){
            if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK){
                return;
            }

            if (mainHasAction){
                return;
            }

            if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR){
                if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
                    if (main.getType() == Material.BRUSH){
                        return;
                    }
                }
                if (isBlockingItem(main)){
                    return;
                }
            }
        }

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR){
            itemAction.executeRight(new ItemActionResult(p, i, null, ArenaManager.getArenaOfPlayer(p)));
        }
        else{
            itemAction.executeLeft(new ItemActionResult(p, i, null, ArenaManager.getArenaOfPlayer(p)));
        }
    }

    private boolean isBlockingItem(ItemStack oppositeItem){
        return switch (oppositeItem.getType()) {
            case CROSSBOW, BOW, FISHING_ROD, POTION, SPLASH_POTION, LINGERING_POTION, ENDER_PEARL, ENDER_EYE, SPYGLASS,
                 TRIDENT, SNOWBALL, EGG, WIND_CHARGE, SHIELD -> true;
            default -> oppositeItem.hasItemMeta() && oppositeItem.getItemMeta().hasFood()
                    || oppositeItem.getType().isEdible();
        };
    }
}
