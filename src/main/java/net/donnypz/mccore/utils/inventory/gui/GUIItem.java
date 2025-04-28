package net.donnypz.mccore.utils.inventory.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class GUIItem {

    protected ItemStack item;
    GUI gui;
    int slot;

    protected Consumer<InventoryClickEvent> clickAction;

    public GUIItem(@NotNull GUI gui, int slot, @NotNull ItemStack item, Consumer<InventoryClickEvent> clickAction){
        this.item = item;
        this.gui = gui;
        this.slot = slot;
        this.gui.getInventory().setItem(slot, this.item);
        this.clickAction = clickAction;
        GUIItem existing = gui.getGUIItem(slot);
        if (existing != null){
            existing.remove();
        }
        this.gui.allGUIItems.put(slot, this);
    }


    void executeAction(@NotNull InventoryClickEvent event){
        if (clickAction == null){
            return;
        }
        clickAction.accept(event);
    }

    public void changeItemStack(ItemStack itemStack){
        //gui.allGUIItems.remove(item);
        //gui.allGUIItems.put(itemStack, this);
        this.item = itemStack;
        gui.getInventory().setItem(slot, itemStack);
    }

    public ItemStack getItemStack(){
        return item;
    }

    public boolean hasClickAction(){
        return clickAction != null;
    }


    public void remove(){
        gui.allGUIItems.remove(slot);
        item = null;
        gui = null;
        clickAction = null;
    }
}
