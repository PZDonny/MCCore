package MCCore.utils.InventoryUtils;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class GUIItem {

    ItemStack item;
    GUI gui;
    int slot;

    Consumer<InventoryClickEvent> action;

    public GUIItem(@NotNull GUI gui, int slot, ItemStack item, Consumer<InventoryClickEvent> action){
        this.item = item;
        this.gui = gui;
        this.slot = slot;
        this.gui.getInventory().setItem(slot, this.item);
        this.action = action;
        gui.allGUIItems.put(item, this);
    }


    void executeAction(@NotNull InventoryClickEvent event){
        if (action == null) return;
        action.accept(event);
    }

    public void changeItemStack(ItemStack itemStack){
        gui.allGUIItems.remove(item);
        gui.allGUIItems.put(itemStack, this);
        this.item = itemStack;
        gui.getInventory().setItem(slot, itemStack);
    }

    public boolean hasAction(){
        return action != null;
    }


    public void remove(){
        gui.allGUIItems.remove(item);
        item = null;
        gui = null;
        action = null;
    }
}
