package net.donnypz.mccore.utils.item;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class Listener_ItemDrop implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent e){
        ItemStack item = e.getItemDrop().getItemStack();
        if (ItemUtils.isUndroppable(item)){
            e.setCancelled(true);
        }
    }
}
