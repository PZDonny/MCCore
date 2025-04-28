package net.donnypz.mccore.utils.inventory.gui;

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

import java.util.function.Consumer;

public class InventoryUtils {
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
                for (int i = 0; i<9; i++){
                    inventory.setItem(i, outlineItem);
                }
            }

            case SECONDROW -> {
                for (int i = 9; i<18; i++){
                    inventory.setItem(i, outlineItem);
                }
            }

            case THIRDROW -> {
                for (int i = 18; i<27; i++){
                    inventory.setItem(i, outlineItem);
                }
            }

            case FOURTHROW -> {
                for (int i = 27; i<36; i++){
                    inventory.setItem(i, outlineItem);
                }
            }

            case FIFTHROW -> {
                for (int i = 36; i<45; i++){
                    inventory.setItem(i, outlineItem);
                }
            }

            case SIXTHROW -> {
                for (int i = 45; i<54; i++){
                    inventory.setItem(i, outlineItem);
                }
            }

            case FILL -> {
                for (int i = 0; i< size; i++){
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
