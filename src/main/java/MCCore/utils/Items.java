package MCCore.utils;

import MCCore.Core;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.*;

public final class Items {

    private Items(){}
    public static ItemStack makeItem(Material material){ //Material
        ItemStack i = new ItemStack(material);
        i.setAmount(1);
        return i;
    }

    public static ItemStack makeItem(Material material, int count){ //Material
        ItemStack i = new ItemStack(material);
        i.setAmount(count);
        return i;
    }
    public static ItemStack makeItem(Material material, int count, boolean glow){ //Material + Glow
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

    public static ItemStack makeItem(Material material, int count, String name){ //Material + Name
        ItemStack i = new ItemStack(material);
        ItemMeta meta = i.getItemMeta();
        if (name != null){
            meta.setDisplayName(name);
        }
        i.setItemMeta(meta);

        if (count <= 0) count = 1;
        i.setAmount(count);
        return i;
    }

    public static ItemStack makeItem(Material material, int count, String name, boolean unbreakable){ //Material + Name + Unbreakable
        ItemStack i = new ItemStack(material);
        ItemMeta meta = i.getItemMeta();
        if (name != null){
            meta.setDisplayName(name);
        }
        meta.setUnbreakable(unbreakable);
        i.setItemMeta(meta);

        i.setAmount(count);
        return i;
    }
    public static ItemStack makeItem(Material material, int count, String name, boolean unbreakable, boolean glow){ //Material + Name + Unbreakable + Glow
        ItemStack i = new ItemStack(material);
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

    public static ItemStack makeItem(Material material, int count, String name, String[] lore){ //Material + Name + Lore
        ItemStack i = new ItemStack(material);
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
    public static ItemStack makeItem(Material material, int count, String name, String[] lore, boolean glow){ //Material + Name + Lore + Glow
        ItemStack i = new ItemStack(material);
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

    public static ItemStack makeItem(Material material, int count, String name, boolean unbreakable, String[] lore){ //Material + Name + Unbreakable + Lore
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

    public static ItemStack makeItem(Material material, int count, String name, boolean unbreakable, String[] lore, boolean glow){ //Material + Name + Unbreakable + Lore + Glow
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

    public static ItemStack makeItem(Material material, int count, String name, boolean unbreakable, String[] lore, Map<Enchantment, Integer> enchants){ //Material + Name + Unbreakable + Lore + Enchants
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

    public static void makeGlow(@NotNull ItemStack i){
        ItemMeta meta = i.getItemMeta();
        if (isArmorPiece(i.getType())) meta.addEnchant(Enchantment.RIPTIDE, 1, true);
        else meta.addEnchant(Enchantment.WATER_WORKER, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        i.setItemMeta(meta);
    }
    public static void removeGlow(@NotNull ItemStack i){
        ItemMeta meta = i.getItemMeta();
        if (meta.getEnchants().isEmpty()) return;
        if (!meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) return;
        Map<Enchantment, Integer> enchantments = meta.getEnchants();
        for (Enchantment e : enchantments.keySet()){
            if (isArmorPiece(i.getType()) && e.equals(Enchantment.RIPTIDE)) meta.removeEnchant(e);
            else if (e.equals(Enchantment.WATER_WORKER)) meta.removeEnchant(e);
        }
        meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        i.setItemMeta(meta);
    }

    public static void setDamage(@NotNull ItemStack item, int damage){
        if (item.getItemMeta() instanceof Damageable meta){
            meta.setDamage(damage);
            item.setItemMeta(meta);
        }
    }
    public static void setDisplayName(@NotNull ItemStack item, @NotNull String displayName){
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);
    }

    public static void setLore(@NotNull ItemStack item, String[] lore){
        ItemMeta meta = item.getItemMeta();
        if (lore == null){
            meta.setLore(null);
        }
        else{
            List<String> loreList = new ArrayList<>(Arrays.asList(lore));
            meta.setLore(loreList);
        }
        item.setItemMeta(meta);
    }

    public static void setLore(@Nonnull ItemStack item, List<String> lore){
        ItemMeta meta = item.getItemMeta();
        if (lore == null || lore.isEmpty()){
            meta.setLore(null);
        }
        else{
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
    }

    public static void setDataContainerKey(@NotNull ItemStack item, @NotNull String key, @NotNull Object value, @NotNull PersistentDataType dataType){
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(Core.getInstance(), key.toLowerCase()), dataType, value);
        item.setItemMeta(meta);
    }


    public static Object getDataContainerKey(@NotNull ItemStack item, @NotNull String key, @NotNull PersistentDataType dataType){
        return item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Core.getInstance(), key.toLowerCase()), dataType);
    }

    public static boolean hasDataContainerKey(@NotNull ItemStack item, @NotNull String key, @NotNull PersistentDataType dataType){
        return item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Core.getInstance(), key.toLowerCase()), dataType);
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

    public static boolean isAir(Material material){
        switch (material){
            case AIR:
            case VOID_AIR:
            case CAVE_AIR:
                return true;
            default:
                return false;
        }
    }

    public static boolean isShulkerBox(Material material){
        switch (material){
            case SHULKER_BOX:
            case BLACK_SHULKER_BOX:
            case BLUE_SHULKER_BOX:
            case BROWN_SHULKER_BOX:
            case CYAN_SHULKER_BOX:
            case GRAY_SHULKER_BOX:
            case GREEN_SHULKER_BOX:
            case LIME_SHULKER_BOX:
            case MAGENTA_SHULKER_BOX:
            case ORANGE_SHULKER_BOX:
            case PINK_SHULKER_BOX:
            case PURPLE_SHULKER_BOX:
            case RED_SHULKER_BOX:
            case WHITE_SHULKER_BOX:
            case YELLOW_SHULKER_BOX:
                return true;
            default:
                return false;
        }
    }

    public static boolean isDoor(Material material){
        switch(material){
            case DARK_OAK_DOOR:
            case ACACIA_DOOR:
            case BIRCH_DOOR:
            case CRIMSON_DOOR:
            case IRON_DOOR:
            case JUNGLE_DOOR:
            case MANGROVE_DOOR:
            case OAK_DOOR:
            case SPRUCE_DOOR:
            case WARPED_DOOR:
            case BAMBOO_DOOR:
                return true;
            default:
                return false;
        }
    }

    public static boolean isGate(Material material){
        switch(material){
            case ACACIA_FENCE_GATE:
            case BIRCH_FENCE_GATE:
            case CRIMSON_FENCE_GATE:
            case DARK_OAK_FENCE_GATE:
            case JUNGLE_FENCE_GATE:
            case MANGROVE_FENCE_GATE:
            case OAK_FENCE_GATE:
            case SPRUCE_FENCE_GATE:
            case WARPED_FENCE_GATE:
            case BAMBOO_FENCE_GATE:
                return true;
            default:
                return false;
        }
    }

    public static boolean isSign(Material material){
        switch(material){
            case ACACIA_SIGN:
            case SPRUCE_SIGN:
            case CRIMSON_SIGN:
            case OAK_SIGN:
            case WARPED_SIGN:
            case MANGROVE_SIGN:
            case JUNGLE_SIGN:
            case BIRCH_SIGN:
            case DARK_OAK_SIGN:
            case BAMBOO_SIGN:
            default:
                return false;
        }
    }

    public static boolean isWallSign(Material material){
        switch (material){
            case WARPED_WALL_SIGN:
            case ACACIA_WALL_SIGN:
            case BIRCH_WALL_SIGN:
            case CRIMSON_WALL_SIGN:
            case JUNGLE_WALL_SIGN:
            case OAK_WALL_SIGN:
            case DARK_OAK_WALL_SIGN:
            case MANGROVE_WALL_SIGN:
            case SPRUCE_WALL_SIGN:
            case BAMBOO_WALL_SIGN:
            default:
                return false;
        }
    }

    public static boolean isHangingSign(Material material){
        switch(material){
            case ACACIA_HANGING_SIGN:
            case BAMBOO_HANGING_SIGN:
            case DARK_OAK_HANGING_SIGN:
            case BIRCH_HANGING_SIGN:
            case CRIMSON_HANGING_SIGN:
            case JUNGLE_HANGING_SIGN:
            case OAK_HANGING_SIGN:
            case MANGROVE_HANGING_SIGN:
            case SPRUCE_HANGING_SIGN:
            case WARPED_HANGING_SIGN:
                return true;
            default:
                return false;
        }
    }

    public static boolean isHangingWallSign(Material material){
        switch(material){
            case WARPED_WALL_HANGING_SIGN:
            case ACACIA_WALL_HANGING_SIGN:
            case BAMBOO_WALL_HANGING_SIGN:
            case BIRCH_WALL_HANGING_SIGN:
            case CRIMSON_WALL_HANGING_SIGN:
            case DARK_OAK_WALL_HANGING_SIGN:
            case JUNGLE_WALL_HANGING_SIGN:
            case MANGROVE_WALL_HANGING_SIGN:
            case OAK_WALL_HANGING_SIGN:
            case SPRUCE_WALL_HANGING_SIGN:
                return true;
            default:
                return false;
        }

    }

    public static boolean isTrapdoor(Material material){
        switch (material){
            case ACACIA_TRAPDOOR:
            case BIRCH_TRAPDOOR:
            case CRIMSON_TRAPDOOR:
            case DARK_OAK_TRAPDOOR:
            case IRON_TRAPDOOR:
            case JUNGLE_TRAPDOOR:
            case MANGROVE_TRAPDOOR:
            case OAK_TRAPDOOR:
            case SPRUCE_TRAPDOOR:
            case WARPED_TRAPDOOR:
            case BAMBOO_TRAPDOOR:
                return true;
            default:
                return false;
        }
    }

    public static boolean isCandle(Material material){
        switch (material){
            case CANDLE:
            case CYAN_CANDLE:
            case BLACK_CANDLE:
            case BLUE_CANDLE:
            case BROWN_CANDLE:
            case GRAY_CANDLE:
            case GREEN_CANDLE:
            case LIME_CANDLE:
            case MAGENTA_CANDLE:
            case ORANGE_CANDLE:
            case PINK_CANDLE:
            case PURPLE_CANDLE:
            case RED_CANDLE:
            case WHITE_CANDLE:
            case YELLOW_CANDLE:
            default:
                return false;
        }
    }

    public static boolean isStairs(Material material){
        switch(material){
            case STONE_BRICK_STAIRS:
            case SANDSTONE_STAIRS:
            case SMOOTH_QUARTZ_STAIRS:
            case SPRUCE_STAIRS:
            case STONE_STAIRS:
            case ACACIA_STAIRS:
            case SMOOTH_RED_SANDSTONE_STAIRS:
            case SMOOTH_SANDSTONE_STAIRS:
            case ANDESITE_STAIRS:
            case BIRCH_STAIRS:
            case BLACKSTONE_STAIRS:
            case BRICK_STAIRS:
            case COBBLED_DEEPSLATE_STAIRS:
            case COBBLESTONE_STAIRS:
            case CRIMSON_STAIRS:
            case CUT_COPPER_STAIRS:
            case DARK_OAK_STAIRS:
            case DARK_PRISMARINE_STAIRS:
            case DEEPSLATE_BRICK_STAIRS:
            case DEEPSLATE_TILE_STAIRS:
            case DIORITE_STAIRS:
            case END_STONE_BRICK_STAIRS:
            case GRANITE_STAIRS:
            case JUNGLE_STAIRS:
            case MANGROVE_STAIRS:
            case EXPOSED_CUT_COPPER_STAIRS:
            case MOSSY_COBBLESTONE_STAIRS:
            case MOSSY_STONE_BRICK_STAIRS:
            case MUD_BRICK_STAIRS:
            case NETHER_BRICK_STAIRS:
            case OAK_STAIRS:
            case OXIDIZED_CUT_COPPER_STAIRS:
            case POLISHED_ANDESITE_STAIRS:
            case POLISHED_BLACKSTONE_BRICK_STAIRS:
            case POLISHED_BLACKSTONE_STAIRS:
            case POLISHED_DEEPSLATE_STAIRS:
            case POLISHED_DIORITE_STAIRS:
            case POLISHED_GRANITE_STAIRS:
            case PRISMARINE_BRICK_STAIRS:
            case PRISMARINE_STAIRS:
            case PURPUR_STAIRS:
            case QUARTZ_STAIRS:
            case RED_NETHER_BRICK_STAIRS:
            case RED_SANDSTONE_STAIRS:
            case WARPED_STAIRS:
            case WAXED_CUT_COPPER_STAIRS:
            case WAXED_EXPOSED_CUT_COPPER_STAIRS:
            case WAXED_OXIDIZED_CUT_COPPER_STAIRS:
            case WAXED_WEATHERED_CUT_COPPER_STAIRS:
            case WEATHERED_CUT_COPPER_STAIRS:
            case BAMBOO_MOSAIC_STAIRS:
            case BAMBOO_STAIRS:
                return true;
            default:
                return false;
        }
    }

    public static boolean isSlab(Material material){
        switch(material){
            case SANDSTONE_SLAB:
            case SMOOTH_QUARTZ_SLAB:
            case ACACIA_SLAB:
            case ANDESITE_SLAB:
            case SPRUCE_SLAB:
            case STONE_SLAB:
            case SMOOTH_RED_SANDSTONE_SLAB:
            case SMOOTH_SANDSTONE_SLAB:
            case SMOOTH_STONE_SLAB:
            case STONE_BRICK_SLAB:
            case BIRCH_SLAB:
            case BLACKSTONE_SLAB:
            case BRICK_SLAB:
            case COBBLED_DEEPSLATE_SLAB:
            case COBBLESTONE_SLAB:
            case CRIMSON_SLAB:
            case CUT_COPPER_SLAB:
            case CUT_RED_SANDSTONE_SLAB:
            case CUT_SANDSTONE_SLAB:
            case DARK_OAK_SLAB:
            case DARK_PRISMARINE_SLAB:
            case DEEPSLATE_TILE_SLAB:
            case DIORITE_SLAB:
            case END_STONE_BRICK_SLAB:
            case GRANITE_SLAB:
            case JUNGLE_SLAB:
            case MANGROVE_SLAB:
            case EXPOSED_CUT_COPPER_SLAB:
            case MOSSY_COBBLESTONE_SLAB:
            case MOSSY_STONE_BRICK_SLAB:
            case MUD_BRICK_SLAB:
            case NETHER_BRICK_SLAB:
            case OAK_SLAB:
            case OXIDIZED_CUT_COPPER_SLAB:
            case PETRIFIED_OAK_SLAB:
            case POLISHED_ANDESITE_SLAB:
            case POLISHED_BLACKSTONE_BRICK_SLAB:
            case POLISHED_BLACKSTONE_SLAB:
            case POLISHED_DEEPSLATE_SLAB:
            case POLISHED_DIORITE_SLAB:
            case POLISHED_GRANITE_SLAB:
            case PRISMARINE_BRICK_SLAB:
            case PRISMARINE_SLAB:
            case PURPUR_SLAB:
            case QUARTZ_SLAB:
            case RED_SANDSTONE_SLAB:
            case WARPED_SLAB:
            case RED_NETHER_BRICK_SLAB:
            case WAXED_CUT_COPPER_SLAB:
            case WAXED_EXPOSED_CUT_COPPER_SLAB:
            case WAXED_OXIDIZED_CUT_COPPER_SLAB:
            case WAXED_WEATHERED_CUT_COPPER_SLAB:
            case WEATHERED_CUT_COPPER_SLAB:
            case DEEPSLATE_BRICK_SLAB:
            case BAMBOO_MOSAIC_SLAB:
            case BAMBOO_SLAB:
                return true;
            default:
                return false;
        }
    }
    public static boolean isGlassBlock(Material material){
        switch(material){
            case GLASS:
            case TINTED_GLASS:
            case BLUE_STAINED_GLASS:
            case BLACK_STAINED_GLASS:
            case BROWN_STAINED_GLASS:
            case CYAN_STAINED_GLASS:
            case GRAY_STAINED_GLASS:
            case GREEN_STAINED_GLASS:
            case LIGHT_BLUE_STAINED_GLASS:
            case LIME_STAINED_GLASS:
            case LIGHT_GRAY_STAINED_GLASS:
            case MAGENTA_STAINED_GLASS:
            case ORANGE_STAINED_GLASS:
            case PINK_STAINED_GLASS:
            case PURPLE_STAINED_GLASS:
            case RED_STAINED_GLASS:
            case WHITE_STAINED_GLASS:
            case YELLOW_STAINED_GLASS:
                return true;
            default:
                return false;
        }
    }

    public static boolean isGlassPane(Material material){
        switch(material){
            case GLASS_PANE:
            case GRAY_STAINED_GLASS_PANE:
            case BLACK_STAINED_GLASS_PANE:
            case PINK_STAINED_GLASS_PANE:
            case PURPLE_STAINED_GLASS_PANE:
            case BLUE_STAINED_GLASS_PANE:
            case BROWN_STAINED_GLASS_PANE:
            case CYAN_STAINED_GLASS_PANE:
            case GREEN_STAINED_GLASS_PANE:
            case LIGHT_BLUE_STAINED_GLASS_PANE:
            case LIME_STAINED_GLASS_PANE:
            case MAGENTA_STAINED_GLASS_PANE:
            case ORANGE_STAINED_GLASS_PANE:
            case RED_STAINED_GLASS_PANE:
            case WHITE_STAINED_GLASS_PANE:
            case YELLOW_STAINED_GLASS_PANE:
            case LIGHT_GRAY_STAINED_GLASS_PANE:
                return true;
            default:
                return false;
        }
    }

    public static boolean isLeaves(Material material){
        switch(material){
            case ACACIA_LEAVES:
            case AZALEA_LEAVES:
            case BIRCH_LEAVES:
            case DARK_OAK_LEAVES:
            case FLOWERING_AZALEA_LEAVES:
            case JUNGLE_LEAVES:
            case MANGROVE_LEAVES:
            case OAK_LEAVES:
            case SPRUCE_LEAVES:
            case CHERRY_LEAVES:
                return true;
            default:
                return false;
        }
    }

    public static boolean isMusicDisc(Material material){
        switch(material){
            case MUSIC_DISC_5:
            case MUSIC_DISC_11:
            case MUSIC_DISC_13:
            case MUSIC_DISC_BLOCKS:
            case MUSIC_DISC_CAT:
            case MUSIC_DISC_CHIRP:
            case MUSIC_DISC_FAR:
            case MUSIC_DISC_MALL:
            case MUSIC_DISC_MELLOHI:
            case MUSIC_DISC_OTHERSIDE:
            case MUSIC_DISC_PIGSTEP:
            case MUSIC_DISC_RELIC:
            case MUSIC_DISC_STAL:
            case MUSIC_DISC_STRAD:
            case MUSIC_DISC_WAIT:
            case MUSIC_DISC_WARD:
                return true;
            default:
                return false;
        }
    }

    public static void dyeArmorPiece(ItemStack armorPiece, Color color){
        if (isLeatherUtility(armorPiece.getType()) || armorPiece.getType() == Material.LEATHER_HORSE_ARMOR){
            return;
        }
        LeatherArmorMeta pieceMeta = (LeatherArmorMeta) armorPiece.getItemMeta();
        pieceMeta.setColor(color);
        armorPiece.setItemMeta(pieceMeta);
    }

    public static void setArmorTrim(ItemStack armorPiece, ArmorTrim trim, boolean hideFlags){
        if (isArmorPiece(armorPiece.getType())){
            ArmorMeta meta = (ArmorMeta) armorPiece.getItemMeta();
            meta.setTrim(trim);
            armorPiece.setItemMeta(meta);
            if (trim == null){
                armorPiece.removeItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
            }
            else if (hideFlags) {
                armorPiece.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
            }
        }
    }

    public static boolean isArmorTrimmed(ItemStack armorPiece){
        if (isArmorPiece(armorPiece.getType())){
            ArmorMeta meta = (ArmorMeta) armorPiece.getItemMeta();
            return meta.hasTrim();
        }
        return false;
    }

    public static boolean isArmorTrim(Material material){
        switch(material){
            case COAST_ARMOR_TRIM_SMITHING_TEMPLATE:
            case DUNE_ARMOR_TRIM_SMITHING_TEMPLATE:
            case EYE_ARMOR_TRIM_SMITHING_TEMPLATE:
            case HOST_ARMOR_TRIM_SMITHING_TEMPLATE:
            case RAISER_ARMOR_TRIM_SMITHING_TEMPLATE:
            case RIB_ARMOR_TRIM_SMITHING_TEMPLATE:
            case SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE:
            case SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE:
            case SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE:
            case SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE:
            case SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE:
            case TIDE_ARMOR_TRIM_SMITHING_TEMPLATE:
            case VEX_ARMOR_TRIM_SMITHING_TEMPLATE:
            case WARD_ARMOR_TRIM_SMITHING_TEMPLATE:
            case WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE:
            case WILD_ARMOR_TRIM_SMITHING_TEMPLATE:
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
                return true;
            default:
                return false;
        }
    }


//Armor
    public static boolean isChestplate(Material material){
        switch (material){
            case LEATHER_CHESTPLATE:
            case GOLDEN_CHESTPLATE:
            case CHAINMAIL_CHESTPLATE:
            case IRON_CHESTPLATE:
            case DIAMOND_CHESTPLATE:
            case NETHERITE_CHESTPLATE:
                return true;
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
                return true;
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
                return true;
            default:
                return false;
        }
    }

    public static ArmorType getArmorType(Material material){
        if (isHelmet(material)){
            return ArmorType.HELMET;
        }
        if (isChestplate(material)){
            return ArmorType.CHESPLATE;
        }
        if (isLeggings(material)){
            return ArmorType.LEGGINGS;
        }
        if (isBoots(material)){
            return ArmorType.BOOTS;
        }
        return null;
    }

    public static boolean isArmorPiece(Material material){
        if (isHelmet(material)){
            return true;
        }
        else if (isChestplate(material)){
            return true;
        }
        else if (isLeggings(material)){
            return true;
        }
        else return isBoots(material);
    }


    public enum ArmorType{
        HELMET,
        CHESPLATE,
        LEGGINGS,
        BOOTS;

        public ItemStack get(Player p){
            switch(this){
                case HELMET -> {
                    return p.getInventory().getHelmet();
                }
                case CHESPLATE -> {
                    return p.getInventory().getChestplate();
                }
                case LEGGINGS -> {
                    return p.getInventory().getLeggings();
                }
                case BOOTS -> {
                    return p.getInventory().getBoots();
                }
                default -> {
                    return null;
                }
            }
        }

        public boolean isWearing(Player p){
            switch(this){
                case HELMET -> {
                    return p.getInventory().getHelmet() != null;
                }
                case CHESPLATE -> {
                    return p.getInventory().getChestplate() != null;
                }
                case LEGGINGS -> {
                    return p.getInventory().getLeggings() != null;
                }
                case BOOTS -> {
                    return p.getInventory().getBoots() != null;
                }
                default -> {
                    return false;
                }
            }
        }
    }
}
