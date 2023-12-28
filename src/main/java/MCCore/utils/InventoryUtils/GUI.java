package MCCore.utils.InventoryUtils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;

public abstract class GUI{
    private ClickCancelType clickCancelType = ClickCancelType.ALWAYS;

    private boolean removeOnClose = true;
    Inventory inventory;
    static final HashMap<Inventory, GUI> allGUIs = new HashMap<>();
    final HashMap<ItemStack, GUIItem> allGUIItems = new HashMap<>();

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

    public void setItem(int slot, ItemStack item){
        inventory.setItem(slot, item);
    }

    public void removeOnClose(boolean removeOnClose){
        this.removeOnClose = removeOnClose;
    }

    public boolean isRemovedOnClose() {
        return removeOnClose;
    }

    public void openToPlayer(Player p){
        p.openInventory(inventory);
    }

    public void openToPlayers(Collection<Player> players){
        for (Player p : players){
            p.openInventory(inventory);
        }
    }

    public GUIItem getGUIItem(ItemStack itemStack) {
        return allGUIItems.get(itemStack);
    }

    public void remove(){
        GUI.allGUIs.remove(inventory);
        inventory.clear();
    }

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
