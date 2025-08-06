package net.donnypz.mccore.listener;

import net.donnypz.mccore.utils.inventory.gui.GUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.function.Consumer;

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
        Consumer<InventoryCloseEvent> closeAction = gui.getCloseAction();
        if (closeAction != null){
            closeAction.accept(event);
        }

        if (gui.isRemovedOnClose()){
            gui.remove();
        }
    }
}
