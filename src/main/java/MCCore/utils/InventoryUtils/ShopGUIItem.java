package MCCore.utils.InventoryUtils;

import MCCore.cosmetics.Cosmetic;
import MCCore.utils.Items;
import net.md_5.bungee.api.ChatColor;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ShopGUIItem extends GUIItem{

    final List<String> lore = new ArrayList<>();
    boolean longLore = false;

    public ShopGUIItem(@NotNull ShopGUI gui, int slot, ItemStack item, Cosmetic cosmetic, Document doc) {
        super(gui, slot, item, null);
        lore.add("");
        this.action = determineAction(cosmetic, doc);
    }

    public ShopGUIItem(@NotNull ShopGUI gui, int slot, ItemStack item, Cosmetic cosmetic, Document doc, String selectValue) {
        super(gui, slot, item, null);
        lore.add("");
        this.action = determineAction(cosmetic, doc);
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

    private Consumer<InventoryClickEvent> determineAction(Cosmetic cosmetic, Document doc){
        ShopGUI gui = (ShopGUI) this.gui;
        String itemName = item.getItemMeta().getDisplayName();
    //Already Selected
        if (doc.get(gui.cosmeticSelectedKey).equals(cosmetic.getMongoSelectValue())){
            lore.add(ChatColor.RED+"You already have this "+ChatColor.YELLOW+gui.cosmeticGroupName+ChatColor.RED+" selected!");
            finalizeLore();
            return inventoryClickEvent -> ShopUtils.alreadySelected((Player) inventoryClickEvent.getWhoClicked());
        }



        if (cosmetic.getCosmeticType().isRanked()){
            lore.add(ChatColor.AQUA+"Click to select this "+ChatColor.YELLOW+gui.cosmeticGroupName+ChatColor.AQUA+"!");
            finalizeLore();
            return inventoryClickEvent -> {
                Player p = (Player) inventoryClickEvent.getWhoClicked();
                ShopUtils.selectCosmetic(p, itemName, gui.cosmeticSelectedKey, cosmetic.getMongoSelectValue());
            };
        }

        else{
            longLore = true;
            String mongoCondition;
            String conditionDisplayName;
            if (cosmetic.getCosmeticType() == Cosmetic.CosmeticType.CURRENCY){
                mongoCondition = cosmetic.getCurrencyType().getMongoKey();
                conditionDisplayName = cosmetic.getCurrencyType().getMongoKeyParenthesized();
                lore.add(ChatColor.WHITE+"Price: "+cosmetic.getPrice()+" "+ChatColor.YELLOW+conditionDisplayName);
            }
            else{
                mongoCondition = cosmetic.getCosmeticType().getMongoKey();
                conditionDisplayName = cosmetic.getCosmeticType().getMongoKeyParenthesized();
                lore.add(ChatColor.WHITE+"Requires: "+cosmetic.getPrice()+" "+ChatColor.YELLOW+conditionDisplayName);
            }


        //Unlocked
            if (doc.getList(gui.cosmeticUnlockList, String.class).contains(cosmetic.getCosmeticName())){
                lore.add(ChatColor.AQUA+"Click to select this "+ChatColor.YELLOW+gui.cosmeticGroupName+ChatColor.AQUA+"!");
                finalizeLore();
                return inventoryClickEvent -> {
                    Player p = (Player) inventoryClickEvent.getWhoClicked();
                    ShopUtils.selectCosmetic(p, itemName, gui.cosmeticSelectedKey, cosmetic.getMongoSelectValue());
                };
            }
        //Locked
            else{
                lore.add(ChatColor.RED+"Click to unlock this "+ChatColor.YELLOW+gui.cosmeticGroupName+"!");
                finalizeLore();
                if (doc.getInteger(mongoCondition) >= cosmetic.getPrice()){
                    return inventoryClickEvent -> {
                        Player p = (Player) inventoryClickEvent.getWhoClicked();
                        ShopUtils.purchaseCosmetic(p, itemName, cosmetic.getCosmeticName(), gui.cosmeticUnlockList, mongoCondition, doc.getInteger(mongoCondition), cosmetic.getPrice());
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
}
