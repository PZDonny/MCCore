package net.donnypz.mccore.utils.inventory.gui;

import net.donnypz.mccore.utils.item.ItemBuilder;
import net.donnypz.mccore.utils.item.ItemUtils;
import net.donnypz.mccore.utils.inventory.cosmetic.FieldMinimumCondition;
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

import java.util.List;
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

    public static void setSlotCurrency(@NotNull GUI gui, @NotNull ItemStack itemStack, int slot, @NotNull Document document, @NotNull String fieldName, @NotNull String displayName){
        setSlotCurrency(gui.getInventory(), itemStack, slot, document, fieldName, displayName);
    }

    public static void setSlotCurrency(@NotNull Inventory inventory, @NotNull ItemStack itemStack, int slot, @NotNull Document document, @NotNull String fieldName, @NotNull String displayName){
        if (!(document.get(fieldName) instanceof Integer)) {
            ItemUtils.setLore(itemStack, List.of(Component.text("Failed to fetch "+fieldName+"!", NamedTextColor.RED)));
            inventory.setItem(slot, itemStack);
            return;
        }

        int currencyCount = document.getInteger(fieldName);
        ItemUtils.setLore(itemStack, List.of(MiniMessage.miniMessage().deserialize("<reset><green>You have <yellow>"+currencyCount+" <green>"+displayName).decoration(TextDecoration.ITALIC, false)));
        inventory.setItem(slot, itemStack);
    }

    public static void setSlotFromField(GUI gui, int slot, String field, ItemStack item, String lorePrefix, String loreSuffix, Document document){
        setSlotFromField(gui.getInventory(), slot, field, item, lorePrefix, loreSuffix, document);
    }

    public static void setSlotFromField(GUI gui, int slot, String field, ItemStack itemStack, Component lorePrefix, Component loreSuffix, Document document){
        setSlotFromField(gui.getInventory(), slot, field, itemStack, lorePrefix, loreSuffix, document);
    }

    public static void setSlotFromField(Inventory inventory, int slot, String field, ItemStack item, String lorePrefix, String loreSuffix, Document document){
        setSlotFromField(inventory, slot, field, item, Component.text(lorePrefix), Component.text(loreSuffix), document);
    }

    public static void setSlotFromField(Inventory inventory, int slot, String field, ItemStack item, Component lorePrefix, Component loreSuffix, Document document){
        if (document == null) {
            ItemUtils.setLore(item, List.of(Component.text("Failed to fetch data", NamedTextColor.RED)));
            inventory.setItem(slot, item);
            return;
        }
        Object o = document.get(field);
        Component comp = lorePrefix.color(NamedTextColor.GREEN)
                .append(Component.text(o.toString(), NamedTextColor.YELLOW))
                .append(loreSuffix.color(NamedTextColor.GREEN))
                .decoration(TextDecoration.ITALIC, false);
        ItemUtils.setLore(item, List.of(comp));
        inventory.setItem(slot, item);
    }

    public static void setSlotFromField(GUI gui, int slot, FieldMinimumCondition condition, ItemStack item, String lorePrefix, String loreSuffix, Document document){
        setSlotFromField(gui, slot, condition, item, Component.text(lorePrefix), Component.text(loreSuffix), document);
    }

    public static void setSlotFromField(GUI gui, int slot, FieldMinimumCondition condition, ItemStack item, Component lorePrefix, Component loreSuffix, Document document){
        if (document == null) {
            ItemUtils.setLore(item, List.of(Component.text("Failed to fetch data", NamedTextColor.RED)));
            gui.setItem(slot, item);
            return;
        }
        double value = condition.getPlayerValueFromFields(document);
        Component comp = lorePrefix.color(NamedTextColor.GREEN)
                .append(Component.text(value, NamedTextColor.YELLOW))
                .append(loreSuffix.color(NamedTextColor.GREEN))
                .decoration(TextDecoration.ITALIC, false);
        ItemUtils.setLore(item, List.of(comp));
        gui.setItem(slot, item);
    }


    public static void setExitItemSlot(GUI gui, int slot){
        new GUIItem(gui, slot, new ItemBuilder(Material.BARRIER)
                .setDisplayName(Component.text("Exit", NamedTextColor.RED))
                .build(), click -> {
            click.getWhoClicked().closeInventory();
        });
    }

    public static void setBackItemSlot(GUI gui, int slot, Consumer<InventoryClickEvent> clickAction){
        new GUIItem(gui, slot, new ItemBuilder(Material.FEATHER)
                .setDisplayName(Component.text("Back", NamedTextColor.RED))
                .build(), clickAction);
    }
    public static void setNextItemSlot(GUI gui, int slot, Consumer<InventoryClickEvent> clickAction){
        new GUIItem(gui, slot, new ItemBuilder(Material.ARROW)
                .setDisplayName(Component.text("Next Page", NamedTextColor.GREEN))
                .build(), clickAction);
    }


    public static void setInventoryOutline(GUI gui, ItemStack outlineItem, OutlineType outlineType){
        setInventoryOutline(gui.getInventory(), outlineItem, outlineType);
    }

    public static void setInventoryOutline(GUI gui, Material outlineMaterial, OutlineType outlineType){
        setInventoryOutline(gui.getInventory(), outlineMaterial, outlineType);
    }

    public static void setInventoryOutline(Inventory inventory, Material outlineMaterial, OutlineType outlineType){
        ItemStack item = new ItemStack(outlineMaterial);
        ItemUtils.setTooltipHidden(item, true);
        setInventoryOutline(inventory, item, outlineType);
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
