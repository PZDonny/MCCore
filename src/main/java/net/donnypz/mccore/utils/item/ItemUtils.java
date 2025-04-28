package net.donnypz.mccore.utils.item;

import io.papermc.paper.block.BlockPredicate;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.FoodProperties;
import io.papermc.paper.datacomponent.item.ItemAdventurePredicate;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistrySet;
import net.donnypz.mccore.Core;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.BlockType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.components.UseCooldownComponent;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.List;


public final class ItemUtils {

    private static final NamespacedKey undroppableKey = new NamespacedKey(Core.getInstance(), "isUndroppable");
    public static final NamespacedKey itemActionKey = new NamespacedKey(Core.getInstance(), "itemAction");
    private ItemUtils(){}

    @Deprecated
    public static ItemStack makeItem(Material material){ //Material
        ItemStack i = new ItemStack(material);
        i.setAmount(1);
        return i;
    }

    @Deprecated
    public static ItemStack makeItem(Material material, int count){ //Material
        ItemStack i = new ItemStack(material);
        i.setAmount(count);
        return i;
    }

    @Deprecated
    public static ItemStack makeItem(Material material, int count, boolean enchantmentGlintOverride){ //Material + Glow
        ItemStack i = new ItemStack(material);
        i.setAmount(count);
        ItemUtils.setEnchantmentGlintOverride(i, enchantmentGlintOverride);
        return i;
    }

    @Deprecated
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

    @Deprecated
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

    @Deprecated
    public static ItemStack makeItem(Material material, int count, String name, boolean unbreakable, boolean hasGlint){ //Material + Name + Unbreakable + Glow
        ItemStack i = new ItemStack(material);
        ItemMeta meta = i.getItemMeta();
        if (name != null){
            meta.setDisplayName(name);
        }
        meta.setUnbreakable(unbreakable);
        meta.setEnchantmentGlintOverride(hasGlint);
        i.setItemMeta(meta);

        i.setAmount(count);
        return i;
    }

    @Deprecated
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

    @Deprecated
    public static ItemStack makeItem(Material material, int count, String name, String[] lore, boolean hasGlint){ //Material + Name + Lore + Glow
        ItemStack i = new ItemStack(material);
        ItemMeta meta = i.getItemMeta();
        if (name != null){
            meta.setDisplayName(name);
        }
        if (lore != null){
            List<String> lorelist = new ArrayList<>(Arrays.asList(lore));
            meta.setLore(lorelist);
        }
        meta.setEnchantmentGlintOverride(hasGlint);
        i.setItemMeta(meta);

        i.setAmount(count);
        return i;
    }

    @Deprecated
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

    @Deprecated
    public static ItemStack makeItem(Material material, int count, String name, boolean unbreakable, String[] lore, boolean hasGlint){ //Material + Name + Unbreakable + Lore + Glow
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
        meta.setEnchantmentGlintOverride(hasGlint);
        i.setItemMeta(meta);

        i.setAmount(count);
        return i;
    }

    @Deprecated
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

    public static ItemStack setEnchantmentGlintOverride(@NotNull ItemStack item, boolean override){
        item.editMeta(meta ->{
            meta.setEnchantmentGlintOverride(override);
        });
        return item;
    }

    public static ItemStack setUnbreakable(@NotNull ItemStack item, boolean unbreakable){
        item.editMeta(meta ->{
            meta.setUnbreakable(unbreakable);
        });
        return item;
    }


    public static ItemStack setFood(@NotNull ItemStack item, @NotNull FoodProperties.Builder builder){
        item.setData(DataComponentTypes.FOOD, builder);
        return item;
    }

    public static ItemStack setFood(@NotNull ItemStack item, boolean alwaysEat, int nutrition, float saturation){
        return setFood(item, FoodProperties
                .food()
                .canAlwaysEat(alwaysEat)
                .nutrition(nutrition)
                .saturation(saturation));
    }

    public static ItemStack setConsumable(@NotNull ItemStack item, @NotNull Consumable.Builder builder){
        item.setData(DataComponentTypes.CONSUMABLE, builder);
        return item;
    }

    public static ItemStack setConsumable(@NotNull ItemStack item, ItemUseAnimation animation, float consumeSeconds, boolean showParticles, @NotNull Key sound, Collection<ConsumeEffect> consumeEffects){
        Consumable.Builder builder = Consumable
                .consumable()
                .animation(animation)
                .consumeSeconds(consumeSeconds)
                .hasConsumeParticles(showParticles)
                .sound(sound);

        if (consumeEffects != null){
            for (ConsumeEffect effect : consumeEffects){
                builder.addEffect(effect);
            }
        }
        return setConsumable(item, builder);
    }

    public ItemStack setConsumable(@NotNull ItemStack item, ItemUseAnimation animation, float consumeSeconds, boolean showParticles, @NotNull Key sound, ConsumeEffect consumeEffect){
        if (consumeEffect != null){
            return setConsumable(item, animation, consumeSeconds, showParticles, sound, List.of(consumeEffect));
        }
        else{
            return setConsumable(item, animation, consumeSeconds, showParticles, sound, (Collection<ConsumeEffect>) null);
        }

    }

    public ItemStack setConsumable(@NotNull ItemStack item, ItemUseAnimation animation, float consumeSeconds, boolean showParticles, @NotNull Sound sound, Collection<ConsumeEffect> consumeEffects){
        return setConsumable(item, animation, consumeSeconds, showParticles, Registry.SOUNDS.getKey(sound), consumeEffects);
    }

    public ItemStack setConsumable(@NotNull ItemStack item, ItemUseAnimation animation, float consumeSeconds, boolean showParticles, @NotNull Sound sound, ConsumeEffect consumeEffect){
        return setConsumable(item, animation, consumeSeconds, showParticles, Registry.SOUNDS.getKey(sound), consumeEffect);
    }

    public static ItemStack setUseRemaineder(@NotNull ItemStack item, ItemStack newItem){
        item.editMeta(meta -> {
            meta.setUseRemainder(newItem);
        });
        return item;
    }

    public static ItemStack setItemModel(@NotNull ItemStack item, @NotNull Material material){
        item.editMeta(meta -> {
            meta.setItemModel(material.getKey());
        });
        return item;
    }

    public static ItemStack setGlider(@NotNull ItemStack item, boolean canGlide){
        item.editMeta(meta -> {
            meta.setGlider(canGlide);
        });
        return item;
    }

    public static ItemStack setMaxStackSize(@NotNull ItemStack item, int maxStackSize){
        item.editMeta(meta ->{
            meta.setMaxStackSize(maxStackSize);
        });
        return item;
    }

    public static ItemStack setDamage(@NotNull ItemStack item, int damage){
        item.editMeta(Damageable.class, meta -> {
            meta.setDamage(damage);
        });
        return item;
    }

    public static ItemStack setMaxDamage(@NotNull ItemStack item, int maxDamage){
        item.editMeta(Damageable.class, meta -> {
            meta.setMaxDamage(maxDamage);
        });
        return item;
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

    public static void setTooltipHidden(ItemStack item, boolean hidden){
        item.editMeta(meta -> {
            meta.setHideTooltip(hidden);
        });
    }


    public static ItemStack setUndroppable(ItemStack item, boolean undroppable){
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
        item.editMeta(meta -> {
            UseCooldownComponent comp = meta.getUseCooldown();
            comp.setCooldownSeconds(cooldownInSeconds);
            comp.setCooldownGroup(cooldownGroup);
        });
        return item;
    }

    public static ItemStack setUseCooldown(@NotNull ItemStack item, float cooldownInSeconds, @Nullable String cooldownGroupKey){
        if (cooldownGroupKey == null){
            return setUseCooldown(item, cooldownInSeconds, (NamespacedKey) null);
        }
        else{
            return setUseCooldown(item, cooldownInSeconds, new NamespacedKey(Core.getInstance(), cooldownGroupKey));
        }
    }

    public static ItemStack randomizeUseCooldownGroup(@NotNull ItemStack item){
        UUID randomUUID = UUID.randomUUID();
        item.editMeta(meta -> {
            UseCooldownComponent comp = meta.getUseCooldown();
            comp.setCooldownGroup(new NamespacedKey(Core.getInstance(), randomUUID.toString()));
            meta.setUseCooldown(comp);
        });
        return item;
    }

    public static ItemStack randomizeUseCooldownGroup(@NotNull ItemStack item, @NotNull String namespace){
        UUID randomUUID = UUID.randomUUID();
        item.editMeta(meta -> {
            UseCooldownComponent comp = meta.getUseCooldown();
            comp.setCooldownGroup(new NamespacedKey(namespace, randomUUID.toString()));
            meta.setUseCooldown(comp);
        });
        return item;
    }

    public static ItemStack addCanPlace(@NotNull ItemStack itemStack, @NotNull BlockType blockType, boolean showInToolTip){
        return setItemAdventurePredicate(itemStack, List.of(blockType), showInToolTip, true);
    }

    public static ItemStack addCanPlace(@NotNull ItemStack itemStack, @NotNull List<BlockType> blockTypes, boolean showInToolTip){
        return setItemAdventurePredicate(itemStack, blockTypes, showInToolTip, true);
    }

    public static ItemStack addCanBreak(@NotNull ItemStack itemStack, @NotNull BlockType blockType, boolean showInToolTip){
        return setItemAdventurePredicate(itemStack, List.of(blockType), showInToolTip, false);
    }

    public static ItemStack addCanBreak(@NotNull ItemStack itemStack, @NotNull List<BlockType> blockTypes, boolean showInToolTip){
        return setItemAdventurePredicate(itemStack, blockTypes, showInToolTip, false);
    }

    private static ItemStack setItemAdventurePredicate(ItemStack itemStack, List<BlockType> blockTypes, boolean showInToolTip, boolean isCanPlace){

        ItemAdventurePredicate existing;
        if (isCanPlace){
            existing = itemStack.getData(DataComponentTypes.CAN_PLACE_ON);
        }
        else{
            existing = itemStack.getData(DataComponentTypes.CAN_BREAK);
        }

        ItemAdventurePredicate.Builder builder = ItemAdventurePredicate.itemAdventurePredicate();
        if (existing != null){
            builder.addPredicates(existing.predicates());
        }


        BlockPredicate.Builder predicateBuilder = BlockPredicate.predicate();

        TypedKey<BlockType>[] typedKeys = new TypedKey[blockTypes.size()];
        for (int i = 0; i < blockTypes.size(); i++){
            BlockType blockType = blockTypes.get(i);
            typedKeys[i] = TypedKey.create(RegistryKey.BLOCK, blockType.key());
        }

        predicateBuilder
                .blocks(RegistrySet.keySet(RegistryKey.BLOCK, typedKeys))
                .build();
        builder.addPredicate(predicateBuilder.build());
        builder.showInTooltip(showInToolTip);


        if (isCanPlace){
            itemStack.setData(DataComponentTypes.CAN_PLACE_ON, builder.build());
        }
        else{
            itemStack.setData(DataComponentTypes.CAN_BREAK, builder.build());
        }
        return itemStack;
    }


    public static ItemStack setPDCKey(@NotNull ItemStack item, @NotNull String key, @NotNull Object value, @NotNull PersistentDataType dataType){
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(Core.getInstance(), key.toLowerCase()), dataType, value);
        item.setItemMeta(meta);
        return item;
    }



    public static ItemStack unsetPDCKey(@NotNull ItemStack item, @NotNull String key){
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().remove(new NamespacedKey(Core.getInstance(), key.toLowerCase()));
        item.setItemMeta(meta);
        return item;
    }

    public static <P, C> C getPDCKey(@NotNull ItemStack item, @NotNull String key, @NotNull PersistentDataType<P, C> dataType){
        return getPDCKey(item, new NamespacedKey(Core.getInstance(), key), dataType);
    }

    public static boolean hasPDCKey(@NotNull ItemStack item, @NotNull String key, @NotNull PersistentDataType dataType){
        return item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Core.getInstance(), key.toLowerCase()), dataType);
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

    public static boolean isLog(Material material){
        switch (material){
            case ACACIA_LOG:
            case BIRCH_LOG:
            case CHERRY_LOG:
            case OAK_LOG:
            case SPRUCE_LOG:
            case MANGROVE_LOG:
            case JUNGLE_LOG:
            case DARK_OAK_LOG:
            case CRIMSON_STEM:
            case WARPED_STEM:
                return true;
            default:
                return false;
        }
    }

    public static boolean isStrippedLog(Material material){
        switch (material){
            case STRIPPED_ACACIA_LOG:
            case STRIPPED_BIRCH_LOG:
            case STRIPPED_CHERRY_LOG:
            case STRIPPED_OAK_LOG:
            case STRIPPED_SPRUCE_LOG:
            case STRIPPED_MANGROVE_LOG:
            case STRIPPED_JUNGLE_LOG:
            case STRIPPED_DARK_OAK_LOG:
            case STRIPPED_CRIMSON_STEM:
            case STRIPPED_WARPED_STEM:
                return true;
            default:
                return false;
        }
    }

    public static boolean isWood(Material material){
        switch (material){
            case ACACIA_WOOD:
            case BIRCH_WOOD:
            case CHERRY_WOOD:
            case OAK_WOOD:
            case SPRUCE_WOOD:
            case MANGROVE_WOOD:
            case JUNGLE_WOOD:
            case DARK_OAK_WOOD:
            case CRIMSON_HYPHAE:
            case WARPED_HYPHAE:
                return true;
            default:
                return false;
        }
    }

    public static boolean isStrippedWood(Material material){
        switch (material){
            case STRIPPED_ACACIA_WOOD:
            case STRIPPED_BIRCH_WOOD:
            case STRIPPED_CHERRY_WOOD:
            case STRIPPED_OAK_WOOD:
            case STRIPPED_SPRUCE_WOOD:
            case STRIPPED_MANGROVE_WOOD:
            case STRIPPED_JUNGLE_WOOD:
            case STRIPPED_DARK_OAK_WOOD:
            case STRIPPED_CRIMSON_HYPHAE:
            case STRIPPED_WARPED_HYPHAE:
                return true;
            default:
                return false;
        }
    }

    public static boolean isOre(Material material){
        switch (material){
            case COAL_ORE:
            case COPPER_ORE:
            case EMERALD_ORE:
            case GOLD_ORE:
            case IRON_ORE:
            case LAPIS_ORE:
            case DIAMOND_ORE:
            case REDSTONE_ORE:
            case NETHER_GOLD_ORE:
            case NETHER_QUARTZ_ORE:
                return true;
            default:
                return false;
        }
    }

    public static boolean isDeepslateOre(Material material){
        switch (material){
            case DEEPSLATE_COAL_ORE:
            case DEEPSLATE_COPPER_ORE:
            case DEEPSLATE_EMERALD_ORE:
            case DEEPSLATE_GOLD_ORE:
            case DEEPSLATE_IRON_ORE:
            case DEEPSLATE_LAPIS_ORE:
            case DEEPSLATE_DIAMOND_ORE:
            case DEEPSLATE_REDSTONE_ORE:
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
            case LIGHT_BLUE_SHULKER_BOX:
            case LIGHT_GRAY_SHULKER_BOX:
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
            case CHERRY_DOOR:
            case COPPER_DOOR:
            case EXPOSED_COPPER_DOOR:
            case OXIDIZED_COPPER_DOOR:
            case WAXED_COPPER_DOOR:
            case WAXED_EXPOSED_COPPER_DOOR:
            case WAXED_OXIDIZED_COPPER_DOOR:
            case WAXED_WEATHERED_COPPER_DOOR:
            case WEATHERED_COPPER_DOOR:
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
            case CHERRY_FENCE_GATE:
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
            case CHERRY_SIGN:
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
            case CHERRY_WALL_SIGN:
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
            case CHERRY_HANGING_SIGN:
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
            case CHERRY_WALL_HANGING_SIGN:
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
            case CHERRY_TRAPDOOR:
            case COPPER_TRAPDOOR:
            case EXPOSED_COPPER_TRAPDOOR:
            case OXIDIZED_COPPER_TRAPDOOR:
            case WAXED_COPPER_TRAPDOOR:
            case WAXED_EXPOSED_COPPER_TRAPDOOR:
            case WAXED_OXIDIZED_COPPER_TRAPDOOR:
            case WAXED_WEATHERED_COPPER_TRAPDOOR:
            case WEATHERED_COPPER_TRAPDOOR:
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
            case LIGHT_BLUE_CANDLE:
            case LIGHT_GRAY_CANDLE:
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
            case CHERRY_STAIRS:
            case POLISHED_TUFF_STAIRS:
            case TUFF_BRICK_STAIRS:
            case TUFF_STAIRS:
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
            case CHERRY_SLAB:
            case POLISHED_TUFF_SLAB:
            case TUFF_BRICK_SLAB:
            case TUFF_SLAB:
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

    public static boolean isFlower(Material material){
        switch(material){
            case DANDELION:
            case POPPY:
            case BLUE_ORCHID:
            case ALLIUM:
            case AZURE_BLUET:
            case RED_TULIP:
            case ORANGE_TULIP:
            case WHITE_TULIP:
            case PINK_TULIP:
            case OXEYE_DAISY:
            case CORNFLOWER:
            case LILY_OF_THE_VALLEY:
            case WITHER_ROSE:
            case TORCHFLOWER:
                return true;
            default:
                return false;
        }
    }

    public static boolean isTallFlower(Material material){
        switch(material){
            case LILAC:
            case ROSE_BUSH:
            case PEONY:
            case PITCHER_PLANT:
            case SUNFLOWER:
                return true;
            default:
                return false;
        }
    }

    public static boolean isButton(Material material){
        switch(material){
            case BAMBOO_BUTTON:
            case ACACIA_BUTTON:
            case BIRCH_BUTTON:
            case CHERRY_BUTTON:
            case CRIMSON_BUTTON:
            case DARK_OAK_BUTTON:
            case JUNGLE_BUTTON:
            case MANGROVE_BUTTON:
            case OAK_BUTTON:
            case POLISHED_BLACKSTONE_BUTTON:
            case SPRUCE_BUTTON:
            case STONE_BUTTON:
            case WARPED_BUTTON:
                return true;
            default:
                return false;
        }
    }

    public static boolean isPottedPlant(Material material){
        return material.name().startsWith("POTTED_");
        /*switch(material){
            case POTTED_ALLIUM:
            case POTTED_ACACIA_SAPLING:
            case POTTED_AZALEA_BUSH:
            case POTTED_BAMBOO:
            case POTTED_AZURE_BLUET:
            case POTTED_BIRCH_SAPLING:
            case POTTED_BLUE_ORCHID:
            case POTTED_CACTUS:
            case POTTED_CORNFLOWER:
            case POTTED_BROWN_MUSHROOM:
            case POTTED_CHERRY_SAPLING:
            case POTTED_DANDELION:
            case POTTED_CRIMSON_FUNGUS:
            case POTTED_CRIMSON_ROOTS:
            case POTTED_FERN:
            case POTTED_DEAD_BUSH:
            case POTTED_DARK_OAK_SAPLING:
            case POTTED_FLOWERING_AZALEA_BUSH:
            case POTTED_JUNGLE_SAPLING:
            case POTTED_LILY_OF_THE_VALLEY:
            case POTTED_MANGROVE_PROPAGULE:
            case POTTED_OAK_SAPLING:
            case POTTED_ORANGE_TULIP:
            case POTTED_OXEYE_DAISY:
            case POTTED_POPPY:
            case POTTED_PINK_TULIP:
            case POTTED_RED_MUSHROOM:
            case POTTED_RED_TULIP:
            case POTTED_TORCHFLOWER:
            case POTTED_SPRUCE_SAPLING:
            case POTTED_WARPED_FUNGUS:
            case POTTED_WARPED_ROOTS:
            case POTTED_WHITE_TULIP:
            case POTTED_WITHER_ROSE:
                return true;
            default:
                return false;
        }*/
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
            case BOLT_ARMOR_TRIM_SMITHING_TEMPLATE:
            case FLOW_ARMOR_TRIM_SMITHING_TEMPLATE:
                return true;
            default:
                return false;
        }
    }

    public static boolean isPotterySherd(Material material){
        switch(material){
            case SHEAF_POTTERY_SHERD:
            case SHELTER_POTTERY_SHERD:
            case ANGLER_POTTERY_SHERD:
            case ARCHER_POTTERY_SHERD:
            case ARMS_UP_POTTERY_SHERD:
            case BLADE_POTTERY_SHERD:
            case BREWER_POTTERY_SHERD:
            case BURN_POTTERY_SHERD:
            case DANGER_POTTERY_SHERD:
            case EXPLORER_POTTERY_SHERD:
            case FRIEND_POTTERY_SHERD:
            case HEART_POTTERY_SHERD:
            case HEARTBREAK_POTTERY_SHERD:
            case HOWL_POTTERY_SHERD:
            case MINER_POTTERY_SHERD:
            case MOURNER_POTTERY_SHERD:
            case PLENTY_POTTERY_SHERD:
            case PRIZE_POTTERY_SHERD:
            case SKULL_POTTERY_SHERD:
            case SNORT_POTTERY_SHERD:
            case FLOW_POTTERY_SHERD:
            case GUSTER_POTTERY_SHERD:
            case SCRAPE_POTTERY_SHERD:
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
