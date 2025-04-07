package net.donnypz.mccore.utils;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collection;

public class PlayerUtils {

    public static void sendHPToPlayer(Player attacker, Player victim){
        if (EntityUtils.isNPC(victim)){
            return;
        }
        double vicHealth = ((double) Math.round((victim.getHealth()+victim.getAbsorptionAmount()) * 100) / 100);
        attacker.sendMessage(ChatColor.YELLOW+victim.getDisplayName()+"'s"+ChatColor.RED+" HP"+ChatColor.WHITE+" - "+vicHealth);
    }

    public static boolean hasEnoughSpace(Player player, int requiredSlots, boolean checkArmor, boolean sendMSG){
        int availableSlots = 0;
        for (ItemStack i : player.getInventory().getStorageContents()){
            if (i == null){
                availableSlots++;
            }
        }
        if (checkArmor){
            if (player.getInventory().getHelmet() != null) availableSlots--;
            if (player.getInventory().getChestplate() != null) availableSlots--;
            if (player.getInventory().getLeggings() != null) availableSlots--;
            if (player.getInventory().getBoots() != null) availableSlots--;
        }

        if (requiredSlots <= availableSlots) {
            return true;
        }
        if (sendMSG){
            player.sendMessage(ChatColor.GOLD+"⚠ "+ChatColor.RED+"You do not have enough inventory space! Make room for "+ChatColor.YELLOW+(requiredSlots-availableSlots)+ChatColor.RED+" slot(s)");
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
        }
        return false;
    }


    public static boolean hasEnoughSpace(Player player, Collection<ItemStack> items, boolean checkArmor, boolean sendMSG){
        int availableSlots = 0;
        for (ItemStack i : player.getInventory().getStorageContents()){
            if (i == null){
                availableSlots++;
            }
            else{
                for (ItemStack item : items){
                    if (i.isSimilar(item) && i.getAmount()+item.getAmount() <= item.getMaxStackSize()){
                        availableSlots++;
                    }
                }
            }
        }
        if (checkArmor){
            if (player.getInventory().getHelmet() != null) availableSlots--;
            if (player.getInventory().getChestplate() != null) availableSlots--;
            if (player.getInventory().getLeggings() != null) availableSlots--;
            if (player.getInventory().getBoots() != null) availableSlots--;
        }

        if (items.size() <= availableSlots) {
            return true;
        }
        if (sendMSG){
            player.sendMessage(ChatColor.GOLD+"⚠ "+ChatColor.RED+"You do not have enough inventory space! Make room for "+ChatColor.YELLOW+(items.size()-availableSlots)+ChatColor.RED+" slot(s)");
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
        }
        return false;
    }

    public static boolean hasEnoughSpace(Player player, ItemStack item, boolean checkArmor, boolean sendMSG){
        int availableSlots = 0;
        for (ItemStack i : player.getInventory().getStorageContents()){
            if (i == null){
                availableSlots++;
            }
            else if (i.isSimilar(item)){
                if (i.getAmount()+item.getAmount() <= item.getMaxStackSize()){
                    availableSlots++;
                }
            }

        }
        if (checkArmor){
            if (player.getInventory().getHelmet() != null) availableSlots--;
            if (player.getInventory().getChestplate() != null) availableSlots--;
            if (player.getInventory().getLeggings() != null) availableSlots--;
            if (player.getInventory().getBoots() != null) availableSlots--;
        }

        if (availableSlots > 0){
            return true;
        }

        if (sendMSG){
            player.sendMessage(ChatColor.GOLD+"⚠ "+ChatColor.RED+"You do not have enough inventory space! Make room for "+ChatColor.YELLOW+(1-availableSlots)+ChatColor.RED+" slot(s)");
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
        }
        return false;
    }

    public static ItemStack getPlayerHead(Player p){
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setPlayerProfile(p.getPlayerProfile());
        skull.setItemMeta(skullMeta);
        return skull;
    }
}
