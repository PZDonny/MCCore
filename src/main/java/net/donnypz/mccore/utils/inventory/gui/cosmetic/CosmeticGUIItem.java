package net.donnypz.mccore.utils.inventory.gui.cosmetic;

import net.donnypz.mccore.cosmetics.*;
import net.donnypz.mccore.database.cosmeticConditions.CosmeticCondition;
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

    /**
     * Determine the purchase action that should be taken when a player clicks this gui item.
     */
    private Consumer<InventoryClickEvent> determineClickAction(Cosmetic cosmetic, CosmeticGUI gui, List<Component> upperDescription){
        UUID playerUUID = gui.playerUUID;
        String typeDisplayName = gui.cosmeticTypeDisplayName;
        PlayerData playerData = PlayerData.get(playerUUID);
        Document playerDoc = playerData.getDocument();

        //Cosmetic Description List
        List<Component> lore = upperDescription == null ? new ArrayList<>() : new ArrayList<>(upperDescription);
        lore.addAll(createConditionDescriptions(cosmetic));

        //Get values if field is in a nested document
        Object[] arr = getTrueObjects(playerDoc, gui.selectedField);
        Document selectDoc = (Document) arr[0];
        String selectField = (String) arr[1];

        //Cosmetic Already Selected
        if (cosmetic.getSelectValue().equals(selectDoc.get(selectField))){
            alreadySelectedDescription(lore, typeDisplayName);
            ItemUtils.setEnchantmentGlintOverride(item, true);
            finalizeDescription(lore);

            return event -> CosmeticShopUtils.alreadySelected((Player) event.getWhoClicked());
        }

        //Check if cosmetic is already unlocked
        if (playerData.hasCosmeticUnlocked(cosmetic, gui.unlockCollection)) {
            return canSelect(lore, gui, playerData, cosmetic);
        }

        //Check if CosmeticConditions are met
        for (CosmeticCondition condition : cosmetic.getConditions()){
            if (!condition.meetsCondition(playerDoc, playerUUID)){
                lore.add(Component.text("You do not own this ", NamedTextColor.RED)
                        .append(Component.text(typeDisplayName, NamedTextColor.YELLOW))
                        .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                finalizeDescription(lore);
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
                purchaseDescription(lore, typeDisplayName, false);
                return event -> CosmeticShopUtils.notUnlocked((Player) event.getWhoClicked());
            }
            //Has Enough
            else{
                purchaseDescription(lore, typeDisplayName, true);
                return event -> CosmeticShopUtils.purchaseCosmetic((Player) event.getWhoClicked(), gui, playerData, cosmetic, cosmetic.getCurrency());
            }
        }

        //Allow cosmetic unlock after all conditions are met, and there is no found price
        return canUnlock(lore, gui, cosmetic);
    }



    private void alreadySelectedDescription(List<Component> lore, String typeDisplayName){
        lore.add(Component.text("You already have this ", NamedTextColor.RED)
                .append(Component.text(typeDisplayName, NamedTextColor.YELLOW))
                .append(Component.text(" selected!", NamedTextColor.RED)).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
    }

    private void purchaseDescription(List<Component> lore, String typeDisplayName, boolean canUnlock){
        NamedTextColor color = canUnlock ? NamedTextColor.GREEN : NamedTextColor.RED;
        lore.add(Component.text("Click to unlock this ", color)
                .append(Component.text(typeDisplayName, NamedTextColor.YELLOW)).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        finalizeDescription(lore);
    }

    private Consumer<InventoryClickEvent> canSelect(List<Component> lore, CosmeticGUI gui, PlayerData playerData, Cosmetic cosmetic){
        lore.add(Component.text("Click to select this ", NamedTextColor.AQUA)
                .append(Component.text(gui.cosmeticTypeDisplayName, NamedTextColor.YELLOW))
                .append(Component.text("!", NamedTextColor.AQUA)).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        finalizeDescription(lore);
        return event -> CosmeticShopUtils.selectCosmetic((Player) event.getWhoClicked(), gui, playerData, cosmetic);
    }

    private Consumer<InventoryClickEvent> canUnlock(List<Component> lore, CosmeticGUI gui, Cosmetic cosmetic){
        lore.add(Component.text("Click to unlock this ", NamedTextColor.GREEN)
                .append(Component.text(gui.cosmeticTypeDisplayName, NamedTextColor.YELLOW)).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        finalizeDescription(lore);
        return event -> CosmeticShopUtils.unlockCosmetic((Player) event.getWhoClicked(), gui, cosmetic);
    }

    private @NotNull List<Component> createConditionDescriptions(Cosmetic cosmetic) {
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

    private void finalizeDescription(List<Component> description) {
        item.lore(description);
        changeItemStack(item);
    }

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
}
