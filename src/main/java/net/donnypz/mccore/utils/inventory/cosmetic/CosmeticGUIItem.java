package net.donnypz.mccore.utils.inventory.cosmetic;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CountOptions;
import net.donnypz.mccore.cosmetics.Cosmetic;
import net.donnypz.mccore.utils.ItemBuilder;
import net.donnypz.mccore.utils.ItemUtils;
import net.donnypz.mccore.utils.inventory.gui.GUIItem;
import net.donnypz.playerdbutils.database.MongoUtils;
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CosmeticGUIItem extends GUIItem {
    public CosmeticGUIItem(@NotNull CosmeticGUI gui, int slot, @NotNull ItemStack item, @NotNull Cosmetic cosmetic) {
        super(gui, slot, item, null);
        this.action = determineAction(cosmetic, gui, null);
    }

    public CosmeticGUIItem(@NotNull CosmeticGUI gui, int slot, @NotNull ItemStack item, @NotNull Cosmetic cosmetic, @NotNull List<Component> upperLore) {
        super(gui, slot, item, null);
        this.action = determineAction(cosmetic, gui, upperLore);
    }

    public CosmeticGUIItem(@NotNull CosmeticGUI gui, int slot, @NotNull Material material, @NotNull Cosmetic cosmetic) {
        super(gui, slot, new ItemBuilder(material)
                .setDisplayName(cosmetic.getCosmeticDisplayName())
                .build(), null);
        this.action = determineAction(cosmetic, gui, null);
    }

    public CosmeticGUIItem(@NotNull CosmeticGUI gui, int slot, @NotNull Material material, @NotNull Cosmetic cosmetic, @NotNull List<Component> upperLore) {
        super(gui, slot, new ItemBuilder(material)
                .setDisplayName(cosmetic.getCosmeticDisplayName())
                .build(), null);
        this.action = determineAction(cosmetic, gui, upperLore);
    }

    private void finalizeLore(List<Component> lore){
        item.lore(lore);
        changeItemStack(item);
    }


    private Consumer<InventoryClickEvent> determineAction(Cosmetic cosmetic, CosmeticGUI gui, List<Component> upperLore){
        String typeDisplayName = gui.cosmeticTypeDisplayName;
    //Lore (Description)
        List<Component> lore = new ArrayList<>();
        if (upperLore != null) lore.addAll(upperLore);
        lore.addAll(getConditionLore(cosmetic));

        String uuidString = gui.playerUUID.toString();
        Document playerDoc = MongoUtils.getDocument(gui.playerCollection, "uuid", uuidString);



        //Get value if field is in a nested document
        Document finalDoc = playerDoc;
        String finalField = gui.selectedField;
        String[] split = finalField.split("\\.");
        for (String field : split) {
            if (finalDoc.get(field) instanceof Document d) {
                finalDoc = d;
            }
            else {
                finalField = field;
                break;
            }
        }

        //Cosmetic Already Selected
        if (cosmetic.getValue().equals(finalDoc.get(finalField))){
            lore.add(Component.text("You already have this ", NamedTextColor.RED)
                    .append(Component.text(typeDisplayName, NamedTextColor.YELLOW))
                    .append(Component.text(" selected!", NamedTextColor.RED)).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
            ItemUtils.setEnchantmentGlintOverride(item, true);
            finalizeLore(lore);

            return event -> CosmeticShopUtils.alreadySelected((Player) event.getWhoClicked());
        }

        //Permission Based (For Testing)
        if (cosmetic.hasPermission()){
            return determinePermission(cosmetic, gui, cosmetic.getPermission(), upperLore);
        }

        //Check if cosmetic is already unlocked
        MongoCollection<Document> unlockCollection = gui.unlockCollection;
        Document unlockedCosmetic = unlockCollection.find(new Document("uuid", uuidString)
                .append(CosmeticGUI.COSMETIC_ID_FIELD, cosmetic.getValue())).first();
        boolean isUnlocked = unlockedCosmetic != null;
        if (isUnlocked){
            return selectable(lore, gui, cosmetic);
        }

        //Check if player meets conditions for cosmetic
        for (FieldMinimumCondition condition : cosmetic.getFieldMinimumConditions()){
            //Condition not met
            if (!meetsCondition(cosmetic, condition, playerDoc)){
                lore.add(Component.text("You do not own this ", NamedTextColor.RED)
                        .append(Component.text(typeDisplayName, NamedTextColor.YELLOW)).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                finalizeLore(lore);
                return event -> {
                    CosmeticShopUtils.notUnlocked((Player) event.getWhoClicked());
                };
            }
        }

        for (DocumentCountCondition condition : cosmetic.getDocumentCountConditions()){
            MongoCollection<Document> collection = condition.getCollection();
            String playerUUIDField = condition.getPlayerUUIDField();
            int count = condition.getCount();
            DocumentCountCondition.CountType type = condition.getType();

            //Get number of documents matching filter in collection (table)
            Document filter = new Document(playerUUIDField, uuidString);
            long result = collection.countDocuments(filter, new CountOptions().limit(count));

            boolean meetsTypeCondition = true;
            if (type == DocumentCountCondition.CountType.AT_LEAST && result < count){
                meetsTypeCondition = false;
            }
            else if (type == DocumentCountCondition.CountType.AT_MOST && result > count){
                meetsTypeCondition = false;
            }
            else if (type == DocumentCountCondition.CountType.EXACT && result != count){
                meetsTypeCondition = false;
            }

            if (!meetsTypeCondition){
                lore.add(Component.text("You do not own this ", NamedTextColor.RED)
                        .append(Component.text(typeDisplayName, NamedTextColor.YELLOW)).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                finalizeLore(lore);
                return event -> {
                    CosmeticShopUtils.notUnlocked((Player) event.getWhoClicked());
                };
            }
        }

        //Check if player has enough currency
        if (cosmetic.hasPrice()){
            String currencyField = cosmetic.getCurrencyField();
            int price = cosmetic.getPrice();
            int funds = playerDoc.getInteger(currencyField);

            //Not Enough
            if (funds < price) {
                unlockLore(lore, typeDisplayName, false);
                return event -> CosmeticShopUtils.notUnlocked((Player) event.getWhoClicked());
            }
            //Has Enough
            else{
                unlockLore(lore, typeDisplayName, true);
                return event -> CosmeticShopUtils.purchaseCosmetic((Player) event.getWhoClicked(), gui, cosmetic, playerDoc);
            }
        }

        //Allow unlock after all conditions are met (And no price is attached)
        return unlockable(lore, gui, cosmetic);
    }




    private void unlockLore(List<Component> lore, String typeDisplayName, boolean canUnlock){
        NamedTextColor color = canUnlock ? NamedTextColor.GREEN : NamedTextColor.RED;
        lore.add(Component.text("Click to unlock this ", color)
                .append(Component.text(typeDisplayName, NamedTextColor.YELLOW)).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        finalizeLore(lore);
    }

    private Consumer<InventoryClickEvent> unlockable(List<Component> lore, CosmeticGUI gui, Cosmetic cosmetic){
        lore.add(Component.text("Click to unlock this ", NamedTextColor.GREEN)
                .append(Component.text(gui.cosmeticTypeDisplayName, NamedTextColor.YELLOW)).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        finalizeLore(lore);
        return event -> CosmeticShopUtils.unlockCosmetic((Player) event.getWhoClicked(), gui, cosmetic);
    }

    private Consumer<InventoryClickEvent> selectable(List<Component> lore, CosmeticGUI gui, Cosmetic cosmetic){
        lore.add(Component.text("Click to select this ", NamedTextColor.AQUA)
                .append(Component.text(gui.cosmeticTypeDisplayName, NamedTextColor.YELLOW))
                .append(Component.text("!", NamedTextColor.AQUA)).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        finalizeLore(lore);
        return event -> CosmeticShopUtils.selectCosmetic((Player) event.getWhoClicked(), gui, cosmetic);
    }

    private boolean meetsCondition(Cosmetic cosmetic, FieldMinimumCondition condition, Document document){
        double result = 0;
        for (String field : condition.getFields()){
            result += ((Number) document.get(field)).doubleValue();
        }
        return result >= condition.getMinimumValue(cosmetic).doubleValue();
    }

    private boolean meetsCondition(String field, Number value, Document document){
        Number retrievedValue = document.get(field, Number.class);
        return retrievedValue.doubleValue() >= value.doubleValue();
    }

    private @NotNull List<Component> getConditionLore(Cosmetic cosmetic) {
        List<Component> conditionLore = new ArrayList<>();

        //Currency Condition
        if (cosmetic.hasPrice()){
            conditionLore.add(MiniMessage.miniMessage().deserialize("<white>Price: <yellow>"+cosmetic.getPrice()).decoration(TextDecoration.ITALIC, false));
        }
        else{
            conditionLore.add(Component.empty());
        }

        for (FieldMinimumCondition condition : cosmetic.getFieldMinimumConditions()){
            Component lore = Component.text("Requires: ", NamedTextColor.GRAY)
                    .append(Component.text(String.valueOf(condition.getMinimumValue(cosmetic)), NamedTextColor.YELLOW))
                    .append(Component.space())
                    .append(Component.text(condition.conditionDisplayName(), NamedTextColor.GRAY))
                    .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
            conditionLore.add(lore);
        }

        for (DocumentCountCondition condition : cosmetic.getDocumentCountConditions()){
            String type;
            DocumentCountCondition.CountType ct = condition.getType();
            if (ct == DocumentCountCondition.CountType.EXACT){
                type = "(Exactly)";
            }
            else if (ct == DocumentCountCondition.CountType.AT_LEAST){
                type = "(At Least)";
            }
            else{
                type = "(At Most)";
            }
            Component lore = Component.text("Requires "+type+": ", NamedTextColor.GRAY)
                    .append(Component.text(String.valueOf(condition.getCount()), NamedTextColor.YELLOW))
                    .append(Component.space())
                    .append(Component.text(condition.conditionDisplayName(), NamedTextColor.GRAY))
                    .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
            conditionLore.add(lore);
        }
        return conditionLore;
    }

    private Consumer<InventoryClickEvent> determinePermission(Cosmetic cosmetic, CosmeticGUI gui, String permission, List<Component> upperLore){
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
                CosmeticShopUtils.selectCosmetic(p, gui, cosmetic);
            }
            else{
                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
                p.sendMessage(Component.text("You do not have permission to select that cosmetic", NamedTextColor.RED));
                p.closeInventory();
            }
        };
    }
}
