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
    public static void resetCosmetic(Player p, String mongoValue, Object resetValue, String cosmeticCategory, MinigameHandler handler){
        p.sendMessage(ChatColor.GREEN + "Successfully reset "+cosmeticCategory+" to default!");
        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 2f);
        p.closeInventory();
        MongoUtils.updatePlayerOne(mongoValue, resetValue, p, handler);
    }

    public static void purchaseCosmetic(Player p, String displayName, String mongoValue, String cosmeticsMongoList, int playerCurrencyCount, int price, MongoUtils.CurrencyType type, MinigameHandler handler){
        p.sendMessage(ChatColor.GREEN+"You have unlocked "+ChatColor.YELLOW+displayName+ChatColor.GREEN+"!");
        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 2f);
        p.closeInventory();
        HashMap<String, Object> values = new HashMap<>();
        List<String> list = new ArrayList<>(handler.getPlayerCacheDocument(p).getList(cosmeticsMongoList, String.class));
        list.add(mongoValue);
        values.put(type.getMongoKey(), playerCurrencyCount-price);
        values.put(cosmeticsMongoList, list);
        MongoUtils.updatePlayerMany(values, p, handler);
    }

    public static void unlockCosmetic(Player p, String displayName, String mongoValue, String cosmeticsMongoList, MinigameHandler handler){
        p.sendMessage(ChatColor.GREEN+"You have unlocked "+ChatColor.YELLOW+displayName+ChatColor.GREEN+"!");
        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 2f);
        p.closeInventory();
        List<String> list = new ArrayList<>(handler.getPlayerCacheDocument(p).getList(cosmeticsMongoList, String.class));
        list.add(mongoValue);
        MongoUtils.updatePlayerOne(cosmeticsMongoList, list, p, handler);
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

    public static void selectCosmetic(Player p, String displayName, String selectedMongoValue, Object updateValue, MinigameHandler handler){
        p.sendMessage(ChatColor.AQUA+"You have selected "+ChatColor.YELLOW+displayName+ChatColor.AQUA+"!");
        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 2f);
        p.closeInventory();
        MongoUtils.updatePlayerOne(selectedMongoValue, updateValue, p, handler);
    }
}
