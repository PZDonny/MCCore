package MCCore.utils;

import MCCore.Core;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class PlayerUtils {

    public static void sendHPToPlayer(Player p, Player v){
        if (isNPC(v)) return;
        double vicHealth = ((double) Math.round(v.getHealth() * 100) / 100);
        p.sendMessage(ChatColor.YELLOW+v.getDisplayName()+"'s"+ChatColor.RED+" HP"+ChatColor.WHITE+" - "+vicHealth);
    }

    public static void sendHPToPlayer(Player p, Player v, int delayInTicks){
        if (isNPC(v)) return;
        new BukkitRunnable(){
            public void run(){
                double vicHealth = ((double) Math.round(v.getHealth() * 100) / 100);
                p.sendMessage(ChatColor.YELLOW+v.getDisplayName()+"'s"+ChatColor.RED+" HP"+ChatColor.WHITE+" - "+vicHealth);
            }
        }.runTaskLater(Core.getInstance(), delayInTicks);
    }

    public static boolean hasEnoughSpace(Player p, Collection<ItemStack> items, boolean checkArmor, boolean sendMSG){
        int empty = 0;
        for (ItemStack i : p.getInventory().getStorageContents()){
            if (i == null){
                empty++;
            }
        }
        if (checkArmor){
            if (p.getInventory().getHelmet() != null) empty--;
            if (p.getInventory().getChestplate() != null) empty--;
            if (p.getInventory().getLeggings() != null) empty--;
            if (p.getInventory().getBoots() != null) empty--;
        }

        if (items.size() <= empty) {
            return true;
        }
        if (sendMSG){
            p.sendMessage(ChatColor.GOLD+"⚠ "+ChatColor.RED+"You do not have enough inventory space! Make room for "+ChatColor.YELLOW+(items.size()-empty)+ChatColor.RED+" slot(s)");
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
        }
        return false;
    }

    public static boolean hasEnoughSpace(Player p, ItemStack item, boolean checkArmor, boolean sendMSG){
        int empty = 0;
        for (ItemStack i : p.getInventory().getStorageContents()){
            if (i == null){
                empty++;
            }
        }
        if (checkArmor){
            if (p.getInventory().getHelmet() != null) empty--;
            if (p.getInventory().getChestplate() != null) empty--;
            if (p.getInventory().getLeggings() != null) empty--;
            if (p.getInventory().getBoots() != null) empty--;
        }

        if (1 <= empty) {
            return true;
        }
        if (sendMSG){
            p.sendMessage(ChatColor.GOLD+"⚠ "+ChatColor.RED+"You do not have enough inventory space! Make room for "+ChatColor.YELLOW+(1-empty)+ChatColor.RED+" slot(s)");
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
        }
        return false;
    }

    public static boolean isNPC(Player p){
        return p.hasMetadata("NPC");
    }
}
