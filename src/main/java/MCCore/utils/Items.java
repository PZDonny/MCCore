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
            if (isArmorPiece(material)) meta.addEnchant(Enchantment.CHANNELING, 1, true);
            else meta.addEnchant(Enchantment.WATER_WORKER, 1, true);
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
            if (isArmorPiece(material)) meta.addEnchant(Enchantment.CHANNELING, 1, true);
            else meta.addEnchant(Enchantment.WATER_WORKER, 1, true);
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
            if (isArmorPiece(material)) meta.addEnchant(Enchantment.CHANNELING, 1, true);
            else meta.addEnchant(Enchantment.WATER_WORKER, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        i.setItemMeta(meta);

        i.setAmount(count);
        return i;
    }

    public ItemStack makeItem(Material material, int count, String name, boolean unbreakable, String[] lore){ //Material + Name + Unbreakable + Lore
        ItemStack i = new ItemStack(material);
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
        ItemStack i = new ItemStack(material);;
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
            if (isArmorPiece(material)) meta.addEnchant(Enchantment.CHANNELING, 1, true);
            else meta.addEnchant(Enchantment.WATER_WORKER, 1, true);
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


    public static void makeGlow(ItemStack i){
        ItemMeta meta = i.getItemMeta();
        if (isArmorPiece(i.getType())) meta.addEnchant(Enchantment.CHANNELING, 1, true);
        else meta.addEnchant(Enchantment.WATER_WORKER, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        i.setItemMeta(meta);
    }
    public static void removeGlow(ItemStack i){
        ItemMeta meta = i.getItemMeta();
        if (meta.getEnchants().isEmpty()) return;
        if (!meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) return;
        Map<Enchantment, Integer> enchantments = meta.getEnchants();
        for (Enchantment e : enchantments.keySet()){
            if (isArmorPiece(i.getType()) && e.equals(Enchantment.CHANNELING)) meta.removeEnchant(e);
            else if (e.equals(Enchantment.WATER_WORKER)) meta.removeEnchant(e);
        }
        meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        i.setItemMeta(meta);
    }

    public static boolean isArmorPiece(Material material){
        switch (material){
            case TURTLE_HELMET:

            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:

            case GOLDEN_HELMET:
            case GOLDEN_CHESTPLATE:
            case GOLDEN_LEGGINGS:
            case GOLDEN_BOOTS:

            case CHAINMAIL_HELMET:
            case CHAINMAIL_CHESTPLATE:
            case CHAINMAIL_LEGGINGS:
            case CHAINMAIL_BOOTS:

            case IRON_HELMET:
            case IRON_CHESTPLATE:
            case IRON_LEGGINGS:
            case IRON_BOOTS:

            case DIAMOND_HELMET:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:
            case DIAMOND_BOOTS:

            case NETHERITE_HELMET:
            case NETHERITE_CHESTPLATE:
            case NETHERITE_LEGGINGS:
            case NETHERITE_BOOTS:
                return true;
            default:
                return false;
        }
    }

    public static boolean isHelmet(Material material){
        switch (material){
            case LEATHER_HELMET:
            case GOLDEN_HELMET:
            case CHAINMAIL_HELMET:
            case IRON_HELMET:
            case DIAMOND_HELMET:
            case NETHERITE_HELMET:
            case TURTLE_HELMET:
            default:
                return false;
        }
    }

    public static boolean isChestplate(Material material){
        switch (material){
            case LEATHER_CHESTPLATE:
            case GOLDEN_CHESTPLATE:
            case CHAINMAIL_CHESTPLATE:
            case IRON_CHESTPLATE:
            case DIAMOND_CHESTPLATE:
            case NETHERITE_CHESTPLATE:
            default:
                return false;
        }
    }

    public static boolean isLeggings(Material material){
        switch (material){
            case LEATHER_LEGGINGS:
            case GOLDEN_LEGGINGS:
            case CHAINMAIL_LEGGINGS:
            case IRON_LEGGINGS:
            case DIAMOND_LEGGINGS:
            case NETHERITE_LEGGINGS:
            default:
                return false;
        }
    }

    public static boolean isBoots(Material material){
        switch (material){
            case LEATHER_BOOTS:
            case GOLDEN_BOOTS:
            case CHAINMAIL_BOOTS:
            case IRON_BOOTS:
            case DIAMOND_BOOTS:
            case NETHERITE_BOOTS:
            default:
                return false;
        }
    }


    public static boolean isSword(Material material){
        switch (material){
            case WOODEN_SWORD:
            case GOLDEN_SWORD:
            case STONE_SWORD:
            case IRON_SWORD:
            case DIAMOND_SWORD:
            case NETHERITE_SWORD:
                return true;
            default:
                return false;
        }
    }

    public static boolean isAxe(Material material){
        switch (material){
            case WOODEN_AXE:
            case GOLDEN_AXE:
            case STONE_AXE:
            case IRON_AXE:
            case DIAMOND_AXE:
            case NETHERITE_AXE:
                return true;
            default:
                return false;
        }
    }

    public static boolean isPickaxe(Material material){
        switch (material){
            case WOODEN_PICKAXE:
            case GOLDEN_PICKAXE:
            case STONE_PICKAXE:
            case IRON_PICKAXE:
            case DIAMOND_PICKAXE:
            case NETHERITE_PICKAXE:
                return true;
            default:
                return false;
        }
    }

    public static boolean isShovel(Material material){
        switch (material){
            case WOODEN_SHOVEL:
            case GOLDEN_SHOVEL:
            case STONE_SHOVEL:
            case IRON_SHOVEL:
            case DIAMOND_SHOVEL:
            case NETHERITE_SHOVEL:
                return true;
            default:
                return false;
        }
    }

    public static boolean isHoe(Material material){
        switch (material){
            case WOODEN_HOE:
            case GOLDEN_HOE:
            case STONE_HOE:
            case IRON_HOE:
            case DIAMOND_HOE:
            case NETHERITE_HOE:
                return true;
            default:
                return false;
        }
    }

    public static boolean isLeatherUtility(Material material){
        switch (material){
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
            case LEATHER_HORSE_ARMOR:
                return true;
            default:
                return false;
        }
    }

    public static boolean isWoodenUtility(Material material){
        switch (material){
            case WOODEN_AXE:
            case WOODEN_HOE:
            case WOODEN_PICKAXE:
            case WOODEN_SHOVEL:
            case WOODEN_SWORD:
                return true;
            default:
                return false;
        }
    }

    public static boolean isStoneUtility(Material material){
        switch (material){
            case STONE_AXE:
            case STONE_HOE:
            case STONE_PICKAXE:
            case STONE_SHOVEL:
            case STONE_SWORD:
                return true;
            default:
                return false;
        }
    }

    public static boolean isIronUtility(Material material){
        switch (material){
            case IRON_AXE:
            case IRON_HOE:
            case IRON_PICKAXE:
            case IRON_SHOVEL:
            case IRON_SWORD:
            case IRON_HELMET:
            case IRON_CHESTPLATE:
            case IRON_LEGGINGS:
            case IRON_BOOTS:
            case IRON_HORSE_ARMOR:
                return true;
            default:
                return false;
        }
    }
    public static boolean isGoldenUtility(Material material){
        switch (material){
            case GOLDEN_AXE:
            case GOLDEN_HOE:
            case GOLDEN_PICKAXE:
            case GOLDEN_SHOVEL:
            case GOLDEN_SWORD:
            case GOLDEN_HELMET:
            case GOLDEN_CHESTPLATE:
            case GOLDEN_LEGGINGS:
            case GOLDEN_BOOTS:
            case GOLDEN_HORSE_ARMOR:
                return true;
            default:
                return false;
        }
    }

    public static boolean isDiamondUtility(Material material){
        switch (material){
            case DIAMOND_AXE:
            case DIAMOND_HOE:
            case DIAMOND_PICKAXE:
            case DIAMOND_SHOVEL:
            case DIAMOND_SWORD:
            case DIAMOND_HELMET:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:
            case DIAMOND_BOOTS:
            case DIAMOND_HORSE_ARMOR:
                return true;
            default:
                return false;
        }
    }

    public static boolean isNetheriteUtility(Material material){
        switch (material){
            case NETHERITE_AXE:
            case NETHERITE_HOE:
            case NETHERITE_PICKAXE:
            case NETHERITE_SHOVEL:
            case NETHERITE_SWORD:
            case NETHERITE_HELMET:
            case NETHERITE_CHESTPLATE:
            case NETHERITE_LEGGINGS:
            case NETHERITE_BOOTS:
                return true;
            default:
                return false;
        }
    }

    public static boolean isBed(Material material){
        switch (material){
            case BLACK_BED:
            case BLUE_BED:
            case BROWN_BED:
            case CYAN_BED:
            case GRAY_BED:
            case GREEN_BED:
            case LIGHT_BLUE_BED:
            case LIME_BED:
            case MAGENTA_BED:
            case ORANGE_BED:
            case PINK_BED:
            case PURPLE_BED:
            case RED_BED:
            case WHITE_BED:
            case YELLOW_BED:
            case LIGHT_GRAY_BED:
                return true;
            default:
                return false;
        }
    }
}
