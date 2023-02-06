package MCCore.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Items {
    public static boolean hasEnoughSpace(Player p, ItemStack[] list, boolean checkArmor, boolean sendMSG){
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

        if (list.length <= empty) {
            return true;
        }
        if (sendMSG){
            p.sendMessage(ChatColor.GOLD+"⚠ "+ChatColor.RED+"You do not have enough inventory space! Make room for "+ChatColor.YELLOW+(list.length-empty)+ChatColor.RED+" slot(s)");
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

//Make items
    public ItemStack makeItem(Material material, int count){ //Material
        ItemStack i = new ItemStack(material);
        i.setAmount(count);
        return i;
    }
    public ItemStack makeItem(Material material, int count, boolean glow){ //Material + Glow
        ItemStack i = new ItemStack(material);
        i.setAmount(count);
        if (glow){
            ItemMeta meta = i.getItemMeta();
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            i.setItemMeta(meta);
        }
        return i;
    }

    public ItemStack makeItem(Material material, int count, String name){ //Material + Name
        ItemStack i = new ItemStack(material);
        List<String> list = new ArrayList<>();
        ItemMeta meta = i.getItemMeta();
        if (name != null){
            meta.setDisplayName(name);
        }
        i.setItemMeta(meta);

        if (count <= 0) count = 1;
        i.setAmount(count);
        return i;
    }

    public ItemStack makeItem(Material material, int count, String name, boolean unbreakable){ //Material + Name + Unbreakable
        ItemStack i = new ItemStack(material);
        List<String> list = new ArrayList<>();
        ItemMeta meta = i.getItemMeta();
        if (name != null){
            meta.setDisplayName(name);
        }
        meta.setUnbreakable(unbreakable);
        i.setItemMeta(meta);

        i.setAmount(count);
        return i;
    }
    public ItemStack makeItem(Material material, int count, String name, boolean unbreakable, boolean glow){ //Material + Name + Unbreakable + Glow
        ItemStack i = new ItemStack(material);
        List<String> list = new ArrayList<>();
        ItemMeta meta = i.getItemMeta();
        if (name != null){
            meta.setDisplayName(name);
        }
        meta.setUnbreakable(unbreakable);
        if (glow){
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        i.setItemMeta(meta);

        i.setAmount(count);
        return i;
    }

    public ItemStack makeItem(Material material, int count, String name, String[] lore){ //Material + Name + Lore
        ItemStack i = new ItemStack(material);
        List<String> list = new ArrayList<>();
        ItemMeta meta = i.getItemMeta();
        if (name != null){
            meta.setDisplayName(name);
        }
        if (lore != null){
            List<String> lorelist = new ArrayList<>(Arrays.asList(lore));
            meta.setLore(lorelist);
        }
        i.setItemMeta(meta);

        i.setAmount(count);
        return i;
    }
    public ItemStack makeItem(Material material, int count, String name, String[] lore, boolean glow){ //Material + Name + Lore + Glow
        ItemStack i = new ItemStack(material);
        List<String> list = new ArrayList<>();
        ItemMeta meta = i.getItemMeta();
        if (name != null){
            meta.setDisplayName(name);
        }
        if (lore != null){
            List<String> lorelist = new ArrayList<>(Arrays.asList(lore));
            meta.setLore(lorelist);
        }
        if (glow){
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        i.setItemMeta(meta);

        i.setAmount(count);
        return i;
    }

    public ItemStack makeItem(Material material, int count, String name, boolean unbreakable, String[] lore){ //Material + Name + Unbreakable + Lore
        ItemStack i = new ItemStack(material);
        List<String> list = new ArrayList<>();
        ItemMeta meta = i.getItemMeta();
        if (name != null){
            meta.setDisplayName(name);
        }
        if (lore != null){
            List<String> lorelist = new ArrayList<>(Arrays.asList(lore));
            meta.setLore(lorelist);
        }
        meta.setUnbreakable(unbreakable);
        i.setItemMeta(meta);

        i.setAmount(count);
        return i;
    }

    public ItemStack makeItem(Material material, int count, String name, boolean unbreakable, String[] lore, boolean glow){ //Material + Name + Unbreakable + Lore + Glow
        ItemStack i = new ItemStack(material);
        List<String> list = new ArrayList<>();
        ItemMeta meta = i.getItemMeta();
        if (name != null){
            meta.setDisplayName(name);
        }
        if (lore != null){
            List<String> lorelist = new ArrayList<>(Arrays.asList(lore));
            meta.setLore(lorelist);
        }
        meta.setUnbreakable(unbreakable);
        if (glow){
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        i.setItemMeta(meta);

        i.setAmount(count);
        return i;
    }

    public ItemStack makeItem(Material material, int count, String name, boolean unbreakable, String[] lore, Map<Enchantment, Integer> enchants){ //Material + Name + Unbreakable + Lore + Enchants
        ItemStack i = new ItemStack(material);
        List<String> list = new ArrayList<>();
        ItemMeta meta = i.getItemMeta();
        if (name != null){
            meta.setDisplayName(name);
        }
        if (lore != null){
            List<String> lorelist = new ArrayList<>(Arrays.asList(lore));
            meta.setLore(lorelist);
        }
        meta.setUnbreakable(unbreakable);

        if (enchants != null){ //metadata overrides enchantments so do enchantments last then give item
            //i.addEnchantments(enchants);
            for (Enchantment ench : enchants.keySet()){ //Gets a "List" of all the keys in a map
                meta.addEnchant(ench, (enchants.get(ench)), true);
            }
        }
        i.setItemMeta(meta);
        i.setAmount(count);
        return i;
    }




}
