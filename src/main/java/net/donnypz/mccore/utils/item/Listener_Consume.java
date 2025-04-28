package net.donnypz.mccore.utils.item;

import net.donnypz.mccore.minigame.arena.ArenaManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class Listener_Consume implements Listener {


    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e){
        Player p = e.getPlayer();
        //ItemStack item = e.getItem();
        ItemStack item = p.getActiveItem();
        if (ItemUtils.hasItemAction(item)){
            ItemAction action = ItemAction.getItemAction(ItemUtils.getItemActionID(item));
            if (action != null){
                ItemActionResult result = new ItemActionResult(p, item, e.getReplacement(), ArenaManager.getArenaOfPlayer(p));
                action.executeConsume(result);
                e.setCancelled(result.cancelConsume);
            }
        }
    }
}
