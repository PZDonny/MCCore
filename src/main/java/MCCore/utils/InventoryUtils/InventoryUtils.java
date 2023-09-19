package MCCore.utils.InventoryUtils;

import MCCore.MongoUtils;
import MCCore.utils.Items;
import net.md_5.bungee.api.ChatColor;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class InventoryUtils {
    public enum OutlineType{
        TOPROW,
        SECONDROW,
        BORDER;
    }

    public static void setSlotCurrency(GUI gui, int slot, Document document, MongoUtils.CurrencyType currencyType){
        setSlotCurrency(gui.getInventory(), slot, document, currencyType);
    }

    public static void setSlotCurrency(Inventory inventory, int slot, Document document, MongoUtils.CurrencyType currencyType){
        ItemStack itemStack = currencyType.getItemStack().clone();
        if (document == null || !document.containsKey(currencyType.getMongoKey()) || !(document.get(currencyType.getMongoKey()) instanceof Integer)) {
            Items.setLore(itemStack, new String[] {ChatColor.RED+"Failed to fetch "+currencyType.getMongoKey()+"!"});
            inventory.setItem(slot, itemStack);
            return;
        }

        int currencyCount = document.getInteger(currencyType.getMongoKey());
        Items.setLore(itemStack, new String[] {ChatColor.GREEN+"You have "+ChatColor.YELLOW+currencyCount+ChatColor.GREEN+" "+currencyType.getMongoKeyParenthesized()});
        inventory.setItem(slot, itemStack);
    }

    public static void setSlotFromMongoValue(GUI gui, int slot, String mongoValue, ItemStack item, String lorePrefix, String loreSuffix, Document document){
        setSlotFromMongoValue(gui.getInventory(), slot, mongoValue, item, lorePrefix, loreSuffix, document);
    }

    public static void setSlotFromMongoValue(Inventory inventory, int slot, String mongoValue, ItemStack item, String lorePrefix, String loreSuffix, Document document){
        if (document == null) {
            Items.setLore(item, new String[] {ChatColor.RED+"Failed to fetch data!"});
            inventory.setItem(slot, item);
            return;
        }
        Object o = document.get(mongoValue);
        Items.setLore(item, new String[] {lorePrefix+o.toString()+loreSuffix});
        inventory.setItem(slot, item);
    }


    public static void setExitItemSlot(GUI gui, int slot){
        new GUIItem(gui, slot, Items.makeItem(Material.BARRIER, 1, ChatColor.RED+"Exit"), click -> {
            click.getWhoClicked().closeInventory();
        });
    }

    public static void setBackItemSlot(GUI gui, int slot, Consumer<InventoryClickEvent> clickAction){
        new GUIItem(gui, slot, Items.makeItem(Material.FEATHER, 1, ChatColor.RED+"Back"), clickAction);
    }
    public static void setNextItemSlot(GUI gui, int slot, Consumer<InventoryClickEvent> clickAction){
        new GUIItem(gui, slot, Items.makeItem(Material.ARROW, 1, ChatColor.GREEN+"Next Page"), clickAction);
    }


    public static void setInventoryOutline(GUI gui, ItemStack outlineItem, OutlineType outlineType){
        setInventoryOutline(gui.getInventory(), outlineItem, outlineType);
    }

    public static void setInventoryOutline(Inventory inventory, ItemStack outlineItem, OutlineType outlineType){
        int size = inventory.getSize();
        switch(outlineType){
            case TOPROW -> {
                for (int i = 0; i<9; i++){
                    inventory.setItem(i, outlineItem);
                }
            }
            case SECONDROW -> {
                for (int i = 9; i<18; i++){
                    inventory.setItem(i, outlineItem);
                }
            }
            case BORDER ->  {
            //Top
                for (int i = 0; i<9; i++){
                    inventory.setItem(i, outlineItem);
                }
            //Bottom
                for (int i = size-9; i<size; i++){
                    inventory.setItem(i, outlineItem);
                }
            //Sides
                for (int i = 9; i<size; i+=9){
                    inventory.setItem(i, outlineItem);
                    inventory.setItem(i+8, outlineItem);
                }
            }
        }
    }

    public static void setInventoryOutline(GUI gui, Material outlineMaterial, OutlineType outlineType){
        setInventoryOutline(gui.getInventory(), outlineMaterial, outlineType);
    }

    public static void setInventoryOutline(Inventory inventory, Material outlineMaterial, OutlineType outlineType){
        int size = inventory.getSize();
        ItemStack outlineItem = Items.makeItem(outlineMaterial, 1, " ");
        switch(outlineType){
            case TOPROW -> {
                for (int i = 0; i<9; i++){
                    inventory.setItem(i, outlineItem);
                }
            }
            case SECONDROW -> {
                for (int i = 9; i<18; i++){
                    inventory.setItem(i, outlineItem);
                }
            }
            case BORDER ->  {
                //Top
                for (int i = 0; i<9; i++){
                    inventory.setItem(i, outlineItem);
                }
                //Bottom
                for (int i = size-9; i<size; i++){
                    inventory.setItem(i, outlineItem);
                }
                //Sides
                for (int i = 9; i<size; i+=9){
                    inventory.setItem(i, outlineItem);
                    inventory.setItem(i+8, outlineItem);
                }
            }
        }
    }
}
