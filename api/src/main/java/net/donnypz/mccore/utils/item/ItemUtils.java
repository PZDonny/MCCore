package net.donnypz.mccore.utils.item;

import net.donnypz.mccore.version.CoreAPI;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.BlockType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.List;


public final class ItemUtils {

    private static final NamespacedKey undroppableKey = new NamespacedKey(CoreAPI.getPlugin(), "isUndroppable");
    public static final NamespacedKey itemActionKey = new NamespacedKey(CoreAPI.getPlugin(), "itemAction");
    private ItemUtils(){}

    public static ItemStack setEnchantmentGlintOverride(@NotNull ItemStack item, boolean override){
        return CoreAPI.getItemHandler().setEnchantmentGlintOverride(item, override);
    }

    public static ItemStack setUnbreakable(@NotNull ItemStack item, boolean unbreakable){
        item.editMeta(meta ->{
            meta.setUnbreakable(unbreakable);
        });
        return item;
    }

    public static ItemStack setConsumable(@NotNull ItemStack item, @NotNull ItemAnimation animation, float consumeSeconds, boolean showParticles, @NotNull Key sound){
        return CoreAPI.getItemHandler().setConsumable(item, animation, consumeSeconds, showParticles, sound);
    }

    public static ItemStack setConsumable(@NotNull ItemStack item, @NotNull ItemAnimation animation, float consumeSeconds, boolean showParticles, @NotNull Sound sound){
        return setConsumable(item, animation, consumeSeconds, showParticles, sound.key());
    }

    public static ItemStack setUseRemainder(@NotNull ItemStack item, ItemStack newItem){
        return CoreAPI.getItemHandler().setUseRemainder(item, newItem);
    }

    public static ItemStack setItemModel(@NotNull ItemStack item, @NotNull Material material){
        return CoreAPI.getItemHandler().setItemModel(item, material);
    }

    public static ItemStack setGlider(@NotNull ItemStack item, boolean canGlide){
        return CoreAPI.getItemHandler().setGlider(item, canGlide);
    }

    public static ItemStack setMaxStackSize(@NotNull ItemStack item, int maxStackSize){
        return CoreAPI.getItemHandler().setMaxStackSize(item, maxStackSize);
    }

    public static ItemStack setDamage(@NotNull ItemStack item, int damage){
        return CoreAPI.getItemHandler().setDamage(item, damage);
    }

    public static ItemStack setMaxDamage(@NotNull ItemStack item, int maxDamage){
        return CoreAPI.getItemHandler().setMaxDamage(item, maxDamage);
    }

    public static ItemStack setDisplayName(@NotNull ItemStack item, @NotNull Component displayName){
        item.editMeta(meta -> {
            meta.displayName(displayName.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        });
        return item;
    }

    public static ItemStack setLore(@NotNull ItemStack item, @NotNull List<Component> lore){
        item.editMeta(meta -> {
            meta.lore(lore);
        });
        return item;
    }

    public static ItemStack addLore(@NotNull ItemStack item, @NotNull Component lore){
        item.editMeta(meta -> {
            List<Component> l;
            if (meta.hasLore()){
                l = meta.lore();
            }
            else{
                l = new ArrayList<>();
            }
            l.add(lore);
            meta.lore(l);
        });
        return item;
    }

    public static ItemStack unsetLore(@NotNull ItemStack item){
        item.editMeta(meta -> {
            meta.lore(null);
        });
        return item;
    }

    public static void setTooltipHidden(@NotNull ItemStack item, boolean hidden){
        CoreAPI.getItemHandler().setTooltipHidden(item, hidden);
    }


    public static ItemStack setUndroppable(@NotNull ItemStack item, boolean undroppable){
        if (undroppable){
            setPDCKey(item, undroppableKey, true, PersistentDataType.BOOLEAN);
        }
        else{
            unsetPDCKey(item, undroppableKey);
        }
        return item;
    }

    public static boolean isUndroppable(ItemStack item){
        return hasPDCKey(item, undroppableKey, PersistentDataType.BOOLEAN);
    }

    public static ItemStack setUseCooldown(@NotNull ItemStack item, float cooldownInSeconds, @Nullable NamespacedKey cooldownGroup){
        return CoreAPI.getItemHandler().setUseCooldown(item, cooldownInSeconds, cooldownGroup);
    }

    public static ItemStack setUseCooldown(@NotNull ItemStack item, float cooldownInSeconds, @Nullable String cooldownGroupKey){
        if (cooldownGroupKey == null){
            return setUseCooldown(item, cooldownInSeconds, (NamespacedKey) null);
        }
        else{
            return setUseCooldown(item, cooldownInSeconds, new NamespacedKey(CoreAPI.getPlugin(), cooldownGroupKey));
        }
    }

    public static ItemStack addCanPlace(@NotNull ItemStack item, @NotNull BlockType blockType, boolean showInToolTip){
        return addCanPlace(item, Set.of(blockType), showInToolTip);
    }

    public static ItemStack addCanPlace(@NotNull ItemStack item, @NotNull Collection<BlockType> blockTypes, boolean showInToolTip){
        return CoreAPI.getItemHandler().addCanPlace(item, blockTypes, showInToolTip);
    }

    public static ItemStack addCanBreak(@NotNull ItemStack item, @NotNull BlockType blockType, boolean showInToolTip){
        return addCanBreak(item, Set.of(blockType), showInToolTip);
    }

    public static ItemStack addCanBreak(@NotNull ItemStack item, @NotNull Collection<BlockType> blockTypes, boolean showInToolTip){
        return CoreAPI.getItemHandler().addCanBreak(item, blockTypes, showInToolTip);
    }

    public static ItemStack randomizeUseCooldownGroup(@NotNull ItemStack item){
        return CoreAPI.getItemHandler().randomizeUseCooldownGroup(item);
    }

    public static ItemStack randomizeUseCooldownGroup(@NotNull ItemStack item, @NotNull String namespace){
        return CoreAPI.getItemHandler().randomizeUseCooldownGroup(item, namespace);
    }

    public static ItemStack setPDCKey(@NotNull ItemStack item, @NotNull String key, @NotNull Object value, @NotNull PersistentDataType dataType){
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(CoreAPI.getPlugin(), key.toLowerCase()), dataType, value);
        item.setItemMeta(meta);
        return item;
    }



    public static ItemStack unsetPDCKey(@NotNull ItemStack item, @NotNull String key){
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().remove(new NamespacedKey(CoreAPI.getPlugin(), key.toLowerCase()));
        item.setItemMeta(meta);
        return item;
    }

    public static <P, C> C getPDCKey(@NotNull ItemStack item, @NotNull String key, @NotNull PersistentDataType<P, C> dataType){
        return getPDCKey(item, new NamespacedKey(CoreAPI.getPlugin(), key), dataType);
    }

    public static boolean hasPDCKey(@NotNull ItemStack item, @NotNull String key, @NotNull PersistentDataType dataType){
        return item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(CoreAPI.getPlugin(), key.toLowerCase()), dataType);
    }


    public static ItemStack setPDCKey(@NotNull ItemStack item, @NotNull NamespacedKey key, @NotNull Object value, @NotNull PersistentDataType dataType){
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(key, dataType, value);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack unsetPDCKey(@NotNull ItemStack item, @NotNull NamespacedKey key){
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().remove(key);
        item.setItemMeta(meta);
        return item;
    }

    public static <P, C> C getPDCKey(@NotNull ItemStack item, @NotNull NamespacedKey key, @NotNull PersistentDataType<P, C> dataType){
        Object obj = item.getItemMeta().getPersistentDataContainer().get(key, dataType);
        return dataType.getComplexType().cast(obj);
    }

    //Allow null items
    public static boolean hasPDCKey(ItemStack item, @NotNull NamespacedKey key, @NotNull PersistentDataType dataType){
        return item != null && item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(key, dataType);
    }

    public static String getItemActionID(ItemStack itemStack){
        if (!hasPDCKey(itemStack, itemActionKey, PersistentDataType.STRING)){
            return "";
        }
        return getPDCKey(itemStack, itemActionKey, PersistentDataType.STRING);
    }

    public static ItemStack setItemAction(ItemStack itemStack, String itemActionID){
        setPDCKey(itemStack, itemActionKey, itemActionID, PersistentDataType.STRING);
        return itemStack;
    }

    public static boolean hasItemAction(ItemStack itemStack){
        if (itemStack == null || !itemStack.hasItemMeta()){
            return false;
        }
        return hasPDCKey(itemStack, itemActionKey, PersistentDataType.STRING);
    }

    public static void removeItemAction(ItemStack itemStack){
        unsetPDCKey(itemStack, itemActionKey);
    }


    public static boolean isSword(Material material){
        return material.name().endsWith("_SWORD");
    }

    public static boolean isAxe(Material material){
        return material.name().endsWith("_AXE");
    }

    public static boolean isPickaxe(Material material){
        return material.name().endsWith("_PICKAXE");
    }

    public static boolean isShovel(Material material){
        return material.name().endsWith("_SHVOEL");
    }

    public static boolean isHoe(Material material){
        return material.name().endsWith("_HOE");
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

    public static boolean isLog(Material material){
        return !material.name().startsWith("STRIPPED") && (material.name().endsWith("_LOG") || material.name().endsWith("_STEM"));
    }

    public static boolean isStrippedLog(Material material){
        return material.name().startsWith("STRIPPED") && (material.name().endsWith("_LOG") || material.name().endsWith("_STEM"));
    }

    public static boolean isWood(Material material){
        return !material.name().startsWith("STRIPPED") && material.name().endsWith("_WOOD") || material.name().endsWith("_HYPHAE");
    }

    public static boolean isStrippedWood(Material material){
        return material.name().startsWith("STRIPPED") && (material.name().endsWith("_WOOD") || material.name().endsWith("_HYPHAE"));
    }

    public static boolean isOre(Material material){
        return !material.name().startsWith("DEEP") && material.name().endsWith("_ORE");
    }

    public static boolean isDeepslateOre(Material material){
        return material.name().startsWith("DEEP") && material.name().endsWith("_ORE");
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
            case LIGHT_BLUE_SHULKER_BOX:
            case LIGHT_GRAY_SHULKER_BOX:
                return true;
            default:
                return false;
        }
    }

    public static boolean isDoor(Material material){
        return material.name().endsWith("_DOOR");
    }

    public static boolean isGate(Material material){
        return material.name().endsWith("_GATE");
    }

    public static boolean isSign(Material material){
        return material.name().endsWith("_SIGN");
    }

    public static boolean isWallSign(Material material){
        return material.name().endsWith("_WALL_SIGN");
    }

    public static boolean isHangingSign(Material material){
        return material.name().endsWith("HANGING_SIGN");
    }

    public static boolean isHangingWallSign(Material material){
        String name = material.name();
        if (name.length() < 12) return false;
        return name.substring(0, name.length()-12).endsWith("WALL_");
    }

    public static boolean isTrapdoor(Material material){
        return material.name().endsWith("_TRAPDOOR");
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
            case LIGHT_BLUE_CANDLE:
            case LIGHT_GRAY_CANDLE:
            default:
                return false;
        }
    }

    public static boolean isStairs(Material material){
        return material.name().endsWith("_STAIRS");
    }

    public static boolean isSlab(Material material){
        return material.name().endsWith("_SLAB");
    }
    public static boolean isGlassBlock(Material material){
        return material.name().endsWith("GLASS");
    }

    public static boolean isGlassPane(Material material){
        return material.name().endsWith("_PANE");
    }

    public static boolean isLeaves(Material material){
        return material.name().endsWith("_LEAVES");
    }

    public static boolean isButton(Material material){
        return material.name().endsWith("_BUTTON");
    }

    public static boolean isPottedPlant(Material material){
        return material.name().startsWith("POTTED_");
    }

    public static boolean isMusicDisc(Material material){
        return material.name().startsWith("MUSIC_DISC");
    }

    public static void dyeArmorPiece(ItemStack armorPiece, Color color){
        if (isLeatherUtility(armorPiece.getType()) || armorPiece.getType() == Material.LEATHER_HORSE_ARMOR){
            return;
        }
        LeatherArmorMeta pieceMeta = (LeatherArmorMeta) armorPiece.getItemMeta();
        pieceMeta.setColor(color);
        armorPiece.setItemMeta(pieceMeta);
    }

    public static void setArmorTrim(ItemStack armorPiece, @Nullable ArmorTrim trim, boolean hideFlags){
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

    public static void setArmorTrim(ItemStack armorPiece, @NotNull TrimMaterial trimMaterial, @NotNull TrimPattern trimPattern, boolean hideFlags){
        if (isArmorPiece(armorPiece.getType())){
            ArmorMeta meta = (ArmorMeta) armorPiece.getItemMeta();
            meta.setTrim(new ArmorTrim(trimMaterial, trimPattern));
            armorPiece.setItemMeta(meta);
            if (hideFlags) {
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
        return material.name().endsWith("TRIM_SMITHING_TEMPLATE");
    }

    public static boolean isPotterySherd(Material material){
        return material.name().endsWith("_SHERD");
    }

    public static boolean isHelmet(Material material){
        return material.name().endsWith("_HELMET");
    }


//Armor
    public static boolean isChestplate(Material material){
        return material.name().endsWith("_CHESTPLATE");
    }

    public static boolean isLeggings(Material material){
        return material.name().endsWith("_LEGGINGS");
    }

    public static boolean isBoots(Material material){
        return material.name().endsWith("_BOOTS");
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
