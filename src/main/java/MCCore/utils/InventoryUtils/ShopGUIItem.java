package MCCore.utils.InventoryUtils;

import MCCore.cosmetics.Cosmetic;
import MCCore.minigameAPI.MinigameHandler;
import MCCore.utils.Items;
import net.md_5.bungee.api.ChatColor;
import org.bson.Document;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class ShopGUIItem extends GUIItem{

    final List<String> lore = new ArrayList<>();
    boolean longLore = false;
    Consumer<InventoryClickEvent> customSelect;

    public ShopGUIItem(@NotNull ShopGUI gui, int slot, ItemStack item, Cosmetic cosmetic, Document doc, MinigameHandler handler) {
        super(gui, slot, item, null);
        lore.add("");
        this.action = determineAction(cosmetic, doc, handler);
    }

    public ShopGUIItem(@NotNull ShopGUI gui, int slot, ItemStack item, Cosmetic cosmetic, Document doc, Consumer<InventoryClickEvent> selectAction, MinigameHandler handler) {
        super(gui, slot, item, null);
        lore.add("");
        this.customSelect = selectAction;
        this.action = determineAction(cosmetic, doc, handler);
    }

    public ShopGUIItem(@NotNull ShopGUI gui, int slot, ItemStack item, Cosmetic cosmetic, Document doc, Collection<String> unlockConditionKeys, String conditionDisplayName, ConditionType conditionType, MinigameHandler handler) {
        super(gui, slot, item, null);
        lore.add("");
        this.action = determineAction(cosmetic, doc, unlockConditionKeys, conditionDisplayName, conditionType, handler);
    }

    public ShopGUIItem(@NotNull ShopGUI gui, int slot, ItemStack item, Cosmetic cosmetic, Document doc, Collection<String> unlockConditionKeys, String conditionDisplayName, ConditionType conditionType, Consumer<InventoryClickEvent> selectAction, MinigameHandler handler) {
        super(gui, slot, item, null);
        lore.add("");
        this.customSelect = selectAction;
        this.action = determineAction(cosmetic, doc, unlockConditionKeys, conditionDisplayName, conditionType, handler);
    }



    public ShopGUIItem addToLore(String loreMessage){
        if (!longLore){
            lore.add(lore.size()-1, loreMessage);
        }
        else{
            lore.add(lore.size()-2, loreMessage);
        }
        return this;
    }

    public void finalizeLore(){
        Items.setLore(item, lore);
        changeItemStack(item);
    }

    private Consumer<InventoryClickEvent> determineAction(Cosmetic cosmetic, Document doc, MinigameHandler handler){
        ShopGUI gui = (ShopGUI) this.gui;
        String priceLoreLine = "";
        String condition = "";
        String conditionDisplayName = "";
        if (cosmetic.getCosmeticType() == Cosmetic.CosmeticType.CURRENCY){
            condition = cosmetic.getCurrencyType().getMongoKey();
            conditionDisplayName = cosmetic.getCurrencyType().getMongoKeyParenthesized();
            priceLoreLine = ChatColor.WHITE+"Price: "+cosmetic.getPrice()+" "+ChatColor.YELLOW+conditionDisplayName;
        }
        else {

            if (!cosmetic.getCosmeticType().isRanked()){
                condition = cosmetic.getCosmeticType().getMongoKey();
                conditionDisplayName = cosmetic.getCosmeticType().getMongoKeyParenthesized();
                priceLoreLine = ChatColor.WHITE+"Requires: "+cosmetic.getPrice()+" "+ChatColor.YELLOW+conditionDisplayName;
            }
        }


    //Already Selected
        if (doc.get(gui.cosmeticSelectedKey).equals(cosmetic.getMongoSelectValue())){
            if (!cosmetic.getCosmeticType().isRanked()){
                lore.add(priceLoreLine);
            }
            lore.add(ChatColor.RED+"You already have this "+ChatColor.YELLOW+gui.cosmeticGroupName+ChatColor.RED+" selected!");
            finalizeLore();
            return inventoryClickEvent -> ShopUtils.alreadySelected((Player) inventoryClickEvent.getWhoClicked());
        }

    //Ranked
        if (cosmetic.getCosmeticType().isRanked()){
            lore.add(ChatColor.AQUA+"Click to select this "+ChatColor.YELLOW+gui.cosmeticGroupName+ChatColor.AQUA+"!");
            lore.add(ChatColor.DARK_GRAY+"Requires: "+cosmetic.getCosmeticType().getDisplayName()+" "+ChatColor.DARK_GRAY+"rank");
            finalizeLore();
            return inventoryClickEvent -> {
                Player p = (Player) inventoryClickEvent.getWhoClicked();
                if (p.hasPermission(cosmetic.getCosmeticType().getPermission())){
                    ShopUtils.selectCosmetic(p, cosmetic.getCosmeticDisplayName(), gui.cosmeticSelectedKey, cosmetic.getMongoSelectValue(), handler);
                }
                else{
                    p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
                    String rank = null;
                    switch (cosmetic.getCosmeticType()){
                        case IRONRANKED -> rank = ChatColor.GRAY+"Iron";
                        case GOLDRANKED -> rank = ChatColor.GOLD+"Gold";
                        case TNTRANKED -> rank = ChatColor.RED+"T"+ChatColor.WHITE+"N"+ChatColor.RED+"T";
                    }
                    if (rank != null){
                        p.sendMessage(ChatColor.RED+"You need "+rank+ChatColor.RED+" rank to select that cosmetic!");
                    }
                    p.closeInventory();
                }

            };
        }

        else{
            longLore = true;
            lore.add(priceLoreLine);


        //Unlocked
            if (doc.getList(gui.cosmeticUnlockListKey, String.class).contains(cosmetic.getCosmeticName())){
                lore.add(ChatColor.AQUA+"Click to select this "+ChatColor.YELLOW+gui.cosmeticGroupName+ChatColor.AQUA+"!");
                finalizeLore();
                if (customSelect != null){
                    return customSelect;
                }
                return inventoryClickEvent -> {
                    Player p = (Player) inventoryClickEvent.getWhoClicked();
                    ShopUtils.selectCosmetic(p, cosmetic.getCosmeticDisplayName(), gui.cosmeticSelectedKey, cosmetic.getMongoSelectValue(), handler);
                };
            }

        //Locked
            else{
                lore.add(ChatColor.RED+"Click to unlock this "+ChatColor.YELLOW+gui.cosmeticGroupName+"!");
                finalizeLore();
                if (doc.getInteger(condition) >= cosmetic.getPrice()){
                    String finalCondition = condition;
                    return inventoryClickEvent -> {
                        Player p = (Player) inventoryClickEvent.getWhoClicked();
                        if (cosmetic.getCosmeticType() == Cosmetic.CosmeticType.CURRENCY){
                            ShopUtils.purchaseCosmetic(p, cosmetic.getCosmeticDisplayName(), cosmetic.getCosmeticName(), gui.cosmeticUnlockListKey, doc.getInteger(finalCondition), cosmetic.getPrice(), cosmetic.getCurrencyType(), handler);
                        }
                        else{
                            ShopUtils.unlockCosmetic(p, cosmetic.getCosmeticDisplayName(), cosmetic.getCosmeticName(), gui.cosmeticUnlockListKey, handler);
                        }


                    };
                }
                else{
                    return inventoryClickEvent -> {
                        Player p = (Player) inventoryClickEvent.getWhoClicked();
                        ShopUtils.notEnough(p);
                    };
                }
            }
        }
    }


    private Consumer<InventoryClickEvent> determineAction(Cosmetic cosmetic, Document doc, Collection<String> unlockConditionKeys, String conditionDisplayName, ConditionType conditionType, MinigameHandler handler){
        ShopGUI gui = (ShopGUI) this.gui;

        //Already Selected
        if (doc.get(gui.cosmeticSelectedKey).equals(cosmetic.getMongoSelectValue())){
            if (!cosmetic.getCosmeticType().isRanked()){
                lore.add(ChatColor.WHITE+"Requires: "+cosmetic.getPrice()+" "+ChatColor.YELLOW+conditionDisplayName);
            }
            lore.add(ChatColor.RED+"You already have this "+ChatColor.YELLOW+gui.cosmeticGroupName+ChatColor.RED+" selected!");
            finalizeLore();
            return inventoryClickEvent -> ShopUtils.alreadySelected((Player) inventoryClickEvent.getWhoClicked());
        }

        longLore = true;
        lore.add(ChatColor.WHITE+"Requires: "+cosmetic.getPrice()+" "+ChatColor.YELLOW+conditionDisplayName);

        //Unlocked
        if (doc.getList(gui.cosmeticUnlockListKey, String.class).contains(cosmetic.getCosmeticName())){
            lore.add(ChatColor.AQUA+"Click to select this "+ChatColor.YELLOW+gui.cosmeticGroupName+ChatColor.AQUA+"!");
            finalizeLore();
            if (customSelect != null){
                return customSelect;
            }
            return inventoryClickEvent -> {
                Player p = (Player) inventoryClickEvent.getWhoClicked();
                ShopUtils.selectCosmetic(p, cosmetic.getCosmeticDisplayName(), gui.cosmeticSelectedKey, cosmetic.getMongoSelectValue(), handler);
            };
        }

        //Locked
        else{
            lore.add(ChatColor.RED+"Click to unlock this "+ChatColor.YELLOW+gui.cosmeticGroupName+"!");
            finalizeLore();

            int playerValue = 0;

            if (conditionType == ConditionType.ADD){
                for (String condition : unlockConditionKeys){
                    playerValue+=(doc.getInteger(condition));
                }
            }

            else{
                for (String condition : unlockConditionKeys){
                    playerValue = Math.max(doc.getInteger(condition), playerValue);
                }
            }


            if (playerValue >= cosmetic.getPrice()){
                return inventoryClickEvent -> {
                    Player p = (Player) inventoryClickEvent.getWhoClicked();
                    ShopUtils.unlockCosmetic(p, cosmetic.getCosmeticDisplayName(), cosmetic.getCosmeticName(), gui.cosmeticUnlockListKey, handler);
                };
            }
            else{
                return inventoryClickEvent -> {
                    Player p = (Player) inventoryClickEvent.getWhoClicked();
                    ShopUtils.notEnough(p);
                };
            }

        }
    }

    public enum ConditionType{
        ADD,
        OR;
    }

}
