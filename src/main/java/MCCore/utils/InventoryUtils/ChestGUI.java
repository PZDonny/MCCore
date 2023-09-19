package MCCore.utils.InventoryUtils;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class ChestGUI extends GUI{

    private final int rows;
    private final String inventoryName;
    public ChestGUI(int rows, String name){
        Inventory inv = Bukkit.createInventory(null, rows*9, name);
        this.rows = rows;
        this.inventoryName = name;
        setInventory(inv);
    }

    public int getRows(){
        return rows;
    }

    public String getInventoryName(){
        return inventoryName;
    }



}
