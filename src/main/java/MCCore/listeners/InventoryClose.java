package MCCore.listeners;

import MCCore.utils.InventoryUtils.GUI;
import MCCore.utils.InventoryUtils.GUIItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryClose implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onClose(InventoryCloseEvent event){
        if (!(event.getPlayer() instanceof Player)) return;
        GUI gui = GUI.getGUI(event.getInventory());

        if (gui == null || event.getInventory().getViewers().isEmpty()) return;

        for(ItemStack item : gui.getInventory().getContents()){
            GUIItem guiItem = gui.getGUIItem(item);
            if (guiItem == null) continue;
            guiItem.remove();
        }
        gui.remove();
    }
}
