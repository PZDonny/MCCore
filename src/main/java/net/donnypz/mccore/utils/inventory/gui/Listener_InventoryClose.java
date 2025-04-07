package net.donnypz.mccore.utils.inventory.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class Listener_InventoryClose implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onClose(InventoryCloseEvent event){
        if (!(event.getPlayer() instanceof Player)){
            return;
        }

        GUI gui = GUI.getGUI(event.getInventory());

        if (gui == null || event.getInventory().getViewers().isEmpty()){
            return;
        }
        if (gui.closeAction != null){
            gui.closeAction.accept(event);
        }
        if (gui.isRemovedOnClose()){
            gui.remove();
        }
    }
}
