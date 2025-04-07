package net.donnypz.mccore.utils.inventory.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class Listener_InventoryClick implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onClick(InventoryClickEvent event){
        if (!(event.getWhoClicked() instanceof Player)){
            return;
        }
        if (event.getCurrentItem() == null){
            return;
        }
        GUI gui = GUI.getGUI(event.getInventory());

        if (gui == null || event.getClickedInventory() == null){
            return;
        }

        switch(gui.getClickCancelType()){
            case ALWAYS -> {
                event.setCancelled(true);
            }
            case TOP_ONLY -> {
                if (event.getClickedInventory().getType() != InventoryType.PLAYER){
                    event.setCancelled(true);
                }
            }
            case BOTTOM_ONLY -> {
                if (event.getClickedInventory().getType() == InventoryType.PLAYER){
                    event.setCancelled(true);
                }
            }
        }


        if (event.getClickedInventory().getType() == InventoryType.PLAYER
                || event.getClick() == ClickType.NUMBER_KEY) {
            return;
        }

        GUIItem item = gui.getGUIItem(event.getSlot());
        //if (item == null || !item.hasAction() || !item.item.equals(event.getCurrentItem())){
        if (item == null || !item.hasAction()){
            return;
        }

        item.executeAction(event);
    }
}
