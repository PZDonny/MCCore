package net.donnypz.mccore.utils.inventory.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Consumer;

public abstract class GUI{
    private ClickCancelType clickCancelType = ClickCancelType.ALWAYS;
    protected boolean isValid = true;
    private boolean removeOnClose = true;
    Inventory inventory;
    static final HashMap<Inventory, GUI> allGUIs = new HashMap<>();
    final HashMap<Integer, GUIItem> allGUIItems = new HashMap<>();
    Consumer<InventoryCloseEvent> closeAction = null;

    public ClickCancelType getClickCancelType() {
        return clickCancelType;
    }

    public void setClickCancelType(ClickCancelType clickCancelType) {
        this.clickCancelType = clickCancelType;
    }


    public Inventory getInventory(){
        return inventory;
    }

    void setInventory(Inventory inventory){
        if (GUI.allGUIs.containsValue(this)){
            return;
        }
        this.inventory = inventory;
        GUI.allGUIs.put(inventory, this);
    }

    public void setCloseAction(Consumer<InventoryCloseEvent> action){
        this.closeAction = action;
    }

    public Consumer<InventoryCloseEvent> getCloseAction() {
        return closeAction;
    }

    public void setItem(int slot, ItemStack item){
        inventory.setItem(slot, item);
    }

    public void removeOnClose(boolean removeOnClose){
        this.removeOnClose = removeOnClose;
    }

    public boolean isRemovedOnClose() {
        return removeOnClose;
    }

    public boolean isValid() {
        return isValid;
    }

    public void openToPlayer(Player player){
        player.openInventory(inventory);
    }

    public void openToPlayers(Collection<Player> players) {
        for (Player player : players){
            player.openInventory(inventory);
        }
    }

    public GUIItem getGUIItem(int slot){
        return allGUIItems.get(slot);
    }



    public void remove(){
        onRemoval();
        allGUIs.remove(inventory);
        for (GUIItem item : new ArrayList<>(allGUIItems.values())){
            item.remove();
        }
        inventory.clear();
        inventory.close();
        isValid = false;
    }

    abstract void onRemoval();

    public static GUI getGUI(@NotNull Inventory inventory){
        return allGUIs.get(inventory);
    }

    public enum ClickCancelType{
        NEVER,
        BOTTOM_ONLY,
        TOP_ONLY,
        ALWAYS;
    }

}
