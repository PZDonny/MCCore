package MCCore.utils.InventoryUtils;

import MCCore.MongoUtils;
import MCCore.minigameAPI.MinigameHandler;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopUtils {
    public static void resetCosmetic(Player p, String mongoValue, Object resetValue, String cosmeticCategory){
        p.sendMessage(ChatColor.GREEN + "Successfully reset "+cosmeticCategory+" to default!");
        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 2f);
        p.closeInventory();
        MongoUtils.updatePlayerOne(mongoValue, resetValue, p, MinigameHandler.getInstance());
    }

    public static void purchaseCosmetic(Player p, String itemName, String mongoValue, String cosmeticsMongoList, String currencyMongoKey, int playerCurrencyCount, int price){
        p.sendMessage(ChatColor.GREEN+"You have unlocked "+ChatColor.YELLOW+itemName+ChatColor.GREEN+"!");
        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 2f);
        p.closeInventory();
        HashMap<String, Object> values = new HashMap<>();
        List<String> list = new ArrayList<>(MinigameHandler.getInstance().getPlayerCacheDocument(p).getList(cosmeticsMongoList, String.class));
        list.add(mongoValue);
        values.put("shards", playerCurrencyCount-price);
        values.put(cosmeticsMongoList, list);
        MongoUtils.updatePlayerMany(values, p, MinigameHandler.getInstance());
    }

    public static void notEnough(Player p){
        p.sendMessage(ChatColor.RED+"You do not have that cosmetic unlocked");
        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
        p.closeInventory();
    }


    public static void alreadySelected(Player p){
        p.sendMessage(ChatColor.RED+"You already have that cosmetic selected!");
        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
        p.closeInventory();
    }

    public static void selectCosmetic(Player p, String itemName, String selectedMongoValue, Object updateValue){
        p.sendMessage(ChatColor.AQUA+"You have selected "+ChatColor.YELLOW+itemName+ChatColor.AQUA+"!");
        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 2f);
        p.closeInventory();
        MongoUtils.updatePlayerOne(selectedMongoValue, updateValue, p, MinigameHandler.getInstance());
    }
}
