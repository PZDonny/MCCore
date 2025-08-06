package net.donnypz.mccore.utils.inventory;

import net.donnypz.mccore.utils.inventory.gui.GUI;
import net.donnypz.mccore.utils.inventory.gui.GUIItem;
import net.donnypz.mccore.utils.item.ItemBuilder;
import net.donnypz.mccore.utils.item.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Consumer;

public final class InventoryUtils {
    public enum OutlineType{
        TOPROW,
        SECONDROW,
        THIRDROW,
        FOURTHROW,
        FIFTHROW,
        SIXTHROW,
        FILL,
        BORDER;
    }

    private InventoryUtils(){}

    public static void setSlotFromField(@NotNull GUI gui, @NotNull ItemStack itemStack, int slot, @NotNull Document document, @NotNull String fieldName, @NotNull String displayName){
        setSlotFromField(gui.getInventory(), itemStack, slot, document, fieldName, displayName);
    }

    public static void setSlotFromField(@NotNull Inventory inventory, @NotNull ItemStack itemStack, int slot, @NotNull Document document, @NotNull String fieldName, @NotNull String displayName){
        ItemStack i = itemStack.clone();
        if (!(document.get(fieldName) instanceof Integer)) {
            ItemUtils.addLore(i, Component.text("Failed to fetch "+fieldName+"!", NamedTextColor.RED));
            inventory.setItem(slot, i);
            return;
        }

        int currencyCount = document.getInteger(fieldName);
        ItemUtils.addLore(i, MiniMessage.miniMessage().deserialize("<reset><green>You have <yellow>"+currencyCount+" <green>"+displayName).decoration(TextDecoration.ITALIC, false));
        inventory.setItem(slot, i);
    }

    public static void setSlotFromFields(@NotNull GUI gui, @NotNull ItemStack itemStack, int slot, @NotNull Document document, @NotNull Collection<String> fieldNames, @NotNull String displayName){
        setSlotFromFields(gui.getInventory(), itemStack, slot, document, fieldNames, displayName);
    }

    public static void setSlotFromFields(@NotNull Inventory inventory, @NotNull ItemStack itemStack, int slot, @NotNull Document document, @NotNull Collection<String> fieldNames, @NotNull String displayName){
        ItemStack item = itemStack.clone();
        int total = 0;
        for (String field : fieldNames){
            if (!(document.get(field) instanceof Integer i)) {
                ItemUtils.addLore(item, Component.text("Failed to fetch data!", NamedTextColor.RED));
                inventory.setItem(slot, item);
                return;
            }
            total+=i;
        }


        ItemUtils.addLore(item, MiniMessage.miniMessage().deserialize("<reset><green>You have <yellow>"+total+" <green>"+displayName).decoration(TextDecoration.ITALIC, false));
        inventory.setItem(slot, item);
    }

    public static void setExitItemSlot(@NotNull GUI gui, int slot){
        new GUIItem(gui, slot, new ItemBuilder(Material.BARRIER)
                .setDisplayName(Component.text("Exit", NamedTextColor.RED))
                .build(), click -> {
            click.getWhoClicked().closeInventory();
        });
    }

    public static void setBackItemSlot(@NotNull GUI gui, int slot, Consumer<InventoryClickEvent> clickAction){
        new GUIItem(gui, slot, new ItemBuilder(Material.FEATHER)
                .setDisplayName(Component.text("Back", NamedTextColor.RED))
                .build(), clickAction);
    }
    public static void setNextItemSlot(@NotNull GUI gui, int slot, Consumer<InventoryClickEvent> clickAction){
        new GUIItem(gui, slot, new ItemBuilder(Material.ARROW)
                .setDisplayName(Component.text("Next Page", NamedTextColor.GREEN))
                .build(), clickAction);
    }


    public static void setInventoryOutline(@NotNull GUI gui, @NotNull ItemStack outlineItem, @NotNull OutlineType outlineType){
        setInventoryOutline(gui.getInventory(), outlineItem, outlineType);
    }

    public static void setInventoryOutline(@NotNull GUI gui, @NotNull Material outlineMaterial, @NotNull OutlineType outlineType){
        setInventoryOutline(gui.getInventory(), outlineMaterial, outlineType);
    }

    public static void setInventoryOutline(@NotNull Inventory inventory, @NotNull Material outlineMaterial, @NotNull OutlineType outlineType){
        ItemStack item = new ItemStack(outlineMaterial);
        ItemUtils.setTooltipHidden(item, true);
        setInventoryOutline(inventory, item, outlineType);
    }

    public static void setInventoryOutline(@NotNull Inventory inventory, @NotNull ItemStack outlineItem, @NotNull OutlineType outlineType){
        int size = inventory.getSize();
        switch(outlineType){
            case TOPROW -> {
                fill(0, 9, outlineItem, inventory);
            }

            case SECONDROW -> {
                fill(9, 18, outlineItem, inventory);
            }

            case THIRDROW -> {
                fill(18, 27, outlineItem, inventory);
            }

            case FOURTHROW -> {
                fill(27, 36, outlineItem, inventory);
            }

            case FIFTHROW -> {
                fill(36, 45, outlineItem, inventory);
            }

            case SIXTHROW -> {
                fill(45, 54, outlineItem, inventory);
            }

            case FILL -> {
                fill(0, size, outlineItem, inventory);
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

    private static void fill(int start, int end, ItemStack outlineItem, Inventory inventory){
        for (int i = start; i < end; i++){
            inventory.setItem(i, outlineItem);
        }
    }
}
