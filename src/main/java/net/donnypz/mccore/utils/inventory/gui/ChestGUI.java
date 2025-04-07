package net.donnypz.mccore.utils.inventory.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class ChestGUI extends GUI{

    private final int rows;
    private final Component inventoryName;
    public ChestGUI(int rows, Component name){
        Inventory inv = Bukkit.createInventory(null, rows*9, name);
        this.rows = rows;
        this.inventoryName = name;
        setInventory(inv);
    }

    public int getRows(){
        return rows;
    }

    public Component getInventoryName(){
        return inventoryName;
    }


    @Override
    void onRemoval() {}
}
