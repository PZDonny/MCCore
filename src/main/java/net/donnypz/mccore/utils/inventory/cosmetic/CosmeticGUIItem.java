package net.donnypz.mccore.utils.inventory.cosmetic;

import net.donnypz.mccore.cosmetics.*;
import net.donnypz.mccore.cosmetics.conditions.CosmeticCondition;
import net.donnypz.mccore.database.PlayerData;
import net.donnypz.mccore.utils.item.ItemBuilder;
import net.donnypz.mccore.utils.item.ItemUtils;
import net.donnypz.mccore.utils.inventory.gui.GUIItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class CosmeticGUIItem extends GUIItem {
    public CosmeticGUIItem(@NotNull CosmeticGUI gui, int slot, @NotNull Material material, @NotNull Cosmetic cosmetic) {
        this(gui, slot, material, cosmetic, null);
    }

    public CosmeticGUIItem(@NotNull CosmeticGUI gui, int slot, @NotNull Material material, @NotNull Cosmetic cosmetic, @Nullable List<Component> upperLore) {
        this(gui,
                slot,
                new ItemBuilder(material)
                .setDisplayName(cosmetic.getCosmeticDisplayName())
                .build(),
                cosmetic,
                null);
    }

    public CosmeticGUIItem(@NotNull CosmeticGUI gui, int slot, @NotNull ItemStack item, @NotNull Cosmetic cosmetic) {
        this(gui, slot, item, cosmetic, null);
    }

    public CosmeticGUIItem(@NotNull CosmeticGUI gui, int slot, @NotNull ItemStack item, @NotNull Cosmetic cosmetic, @Nullable List<Component> upperLore) {
        super(gui, slot, item, null);
        this.clickAction = determineClickAction(cosmetic, gui, upperLore);
    }

    private void finalizeLore(List<Component> lore){
        item.lore(lore);
        changeItemStack(item);
    }

    //Add onSelect, onPurchasedOrUnlocked


    private Object[] getTrueObjects(Document document, String field){
        if (!field.contains(".")){
            return new Object[]{document, field};
        }
        Document trueDoc = document;
        String[] split = field.split("\\.");
        for (int i = 0; i < split.length-1; i++){
            if (trueDoc.get(split[i]) instanceof Document d) {
                trueDoc = d;
            }
            else {
                break;
            }
        }
        return new Object[]{trueDoc, split[split.length-1]};
    }


    private Consumer<InventoryClickEvent> determineClickAction(Cosmetic cosmetic, CosmeticGUI gui, List<Component> upperLore){
        UUID playerUUID = gui.playerUUID;
        String typeDisplayName = gui.cosmeticTypeDisplayName;
        PlayerData playerData = PlayerData.get(playerUUID);
        Document playerDoc = playerData.getDocument();

        //Cosmetic Description List
        List<Component> lore = upperLore == null ? new ArrayList<>() : new ArrayList<>(upperLore);
        lore.addAll(getConditionLore(cosmetic));

        //Get values if field is in a nested document
        Document selectedFieldDoc = playerDoc;
        String selectedField = gui.selectedField;
        if (selectedField.contains(".")){
            String[] split = selectedField.split("\\.");
            for (int i = 0; i < split.length-1; i++){
                if (selectedFieldDoc.get(split[i]) instanceof Document d) {
                    selectedFieldDoc = d;
                }
                else {
                    break;
                }
            }
            selectedField = split[split.length-1];
        }

        //Cosmetic Already Selected
        if (cosmetic.getSelectValue().equals(selectedFieldDoc.get(selectedField))){
            alreadySelectedLore(lore, typeDisplayName);
            ItemUtils.setEnchantmentGlintOverride(item, true);
            finalizeLore(lore);

            return event -> CosmeticShopUtils.alreadySelected((Player) event.getWhoClicked());
        }

        //Permission Based Condition (For Testing)
        if (cosmetic.hasPermission()){
            return determinePermissionAction(cosmetic, gui, playerData, cosmetic.getPermission(), upperLore);
        }

        //Check if cosmetic is already unlocked
        if (playerData.hasCosmeticUnlocked(cosmetic, gui.unlockCollection)) {
            return selectableLore(lore, gui, playerData, cosmetic);
        }

        //Check if CosmeticConditions are met
        for (CosmeticCondition condition : cosmetic.getConditions()){
            if (!condition.meetsCondition(playerDoc, playerUUID)){
                lore.add(Component.text("You do not own this ", NamedTextColor.RED)
                        .append(Component.text(typeDisplayName, NamedTextColor.YELLOW))
                        .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                finalizeLore(lore);
                return event -> CosmeticShopUtils.notUnlocked((Player) event.getWhoClicked());
            }
        }

        //Check if player has enough currency
        Cosmetic.Currency currency = cosmetic.getCurrency();
        if (currency != null){
            String currencyField = currency.currencyField();
            int price = currency.price();
            int funds = playerDoc.getInteger(currencyField);

            //Not Enough
            if (funds < price) {
                purchaseLore(lore, typeDisplayName, false);
                return event -> CosmeticShopUtils.notUnlocked((Player) event.getWhoClicked());
            }
            //Has Enough
            else{
                purchaseLore(lore, typeDisplayName, true);
                return event -> CosmeticShopUtils.purchaseCosmetic((Player) event.getWhoClicked(), gui, playerData, cosmetic, cosmetic.getCurrency());
            }
        }

        //Allow cosmetic unlock after all conditions are met (And no price is attached)
        return unlockable(lore, gui, cosmetic);
    }



    private void alreadySelectedLore(List<Component> lore, String typeDisplayName){
        lore.add(Component.text("You already have this ", NamedTextColor.RED)
                .append(Component.text(typeDisplayName, NamedTextColor.YELLOW))
                .append(Component.text(" selected!", NamedTextColor.RED)).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
    }

    private void purchaseLore(List<Component> lore, String typeDisplayName, boolean canUnlock){
        NamedTextColor color = canUnlock ? NamedTextColor.GREEN : NamedTextColor.RED;
        lore.add(Component.text("Click to unlock this ", color)
                .append(Component.text(typeDisplayName, NamedTextColor.YELLOW)).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        finalizeLore(lore);
    }

    private Consumer<InventoryClickEvent> selectableLore(List<Component> lore, CosmeticGUI gui, PlayerData playerData, Cosmetic cosmetic){
        lore.add(Component.text("Click to select this ", NamedTextColor.AQUA)
                .append(Component.text(gui.cosmeticTypeDisplayName, NamedTextColor.YELLOW))
                .append(Component.text("!", NamedTextColor.AQUA)).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        finalizeLore(lore);
        return event -> CosmeticShopUtils.selectCosmetic((Player) event.getWhoClicked(), gui, playerData, cosmetic);
    }

    private Consumer<InventoryClickEvent> unlockable(List<Component> lore, CosmeticGUI gui, Cosmetic cosmetic){
        lore.add(Component.text("Click to unlock this ", NamedTextColor.GREEN)
                .append(Component.text(gui.cosmeticTypeDisplayName, NamedTextColor.YELLOW)).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        finalizeLore(lore);
        return event -> CosmeticShopUtils.unlockCosmetic((Player) event.getWhoClicked(), gui, cosmetic);
    }

    private @NotNull List<Component> getConditionLore(Cosmetic cosmetic) {
        List<Component> conditionLore = new ArrayList<>();

        //Currency Condition
        Cosmetic.Currency currency = cosmetic.getCurrency();
        if (currency != null){
            conditionLore.add(MiniMessage.miniMessage().deserialize("<white>Price: <yellow>"+currency.price()+" "+currency.displayName())
                    .decoration(TextDecoration.ITALIC, false));
        }
        else{
            conditionLore.add(Component.empty());
        }

        for (CosmeticCondition condition : cosmetic.getConditions()){
            conditionLore.add(condition.buildLore(cosmetic));
        }

        return conditionLore;
    }

    private Consumer<InventoryClickEvent> determinePermissionAction(Cosmetic cosmetic, CosmeticGUI gui, PlayerData playerData, String permission, List<Component> upperLore){
        List<Component> lore = new ArrayList<>();
        if (upperLore != null){
            lore.addAll(upperLore);
        }

        lore.add(Component.text("Click to select this ", NamedTextColor.AQUA)
                .append(Component.text(gui.cosmeticTypeDisplayName, NamedTextColor.YELLOW))
                .append(Component.text("!", NamedTextColor.AQUA)).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        lore.add(Component.text("Permission: "+cosmetic.getPermission(), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        finalizeLore(lore);

        return inventoryClickEvent -> {
            Player p = (Player) inventoryClickEvent.getWhoClicked();
            if (p.hasPermission(permission)){
                CosmeticShopUtils.selectCosmetic(p, gui, playerData, cosmetic);
            }
            else{
                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
                p.sendMessage(Component.text("You do not have permission to select that cosmetic", NamedTextColor.RED));
                p.closeInventory();
            }
        };
    }
}
