package net.donnypz.mccore.utils.item;

import net.donnypz.mccore.version.CoreAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ItemBuilder implements Cloneable{

    protected final ItemStack itemStack;
    public ItemBuilder(Material material){
        itemStack = new ItemStack(material);
    }

    public ItemBuilder(ItemStack itemStack){
        this.itemStack = itemStack.clone();
    }

    public ItemBuilder addAttributeModifier(Attribute attribute, AttributeModifier modifier){
        itemStack.editMeta(meta ->{
            meta.addAttributeModifier(attribute, modifier);
        });
        return this;
    }

    public ItemBuilder setMaxStackSize(int maxStackSize){
        ItemUtils.setMaxStackSize(itemStack, maxStackSize);
        return this;
    }

    public ItemBuilder setAmount(int amount){
        itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder setDisplayName(Component displayName){
        ItemUtils.setDisplayName(itemStack, displayName);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable){
        ItemUtils.setUnbreakable(itemStack, unbreakable);
        return this;
    }

    public ItemBuilder setUndroppable(boolean undroppable){
        ItemUtils.setUndroppable(itemStack, undroppable);
        return this;
    }

    public ItemBuilder setItemAction(String itemActionID){
        ItemUtils.setItemAction(itemStack, itemActionID);
        return this;
    }

    public ItemBuilder setTooltipHidden(boolean hidden){
        ItemUtils.setTooltipHidden(itemStack, hidden);
        return this;
    }

    public ItemBuilder setPDCKey(@NotNull NamespacedKey key, @NotNull Object value, @NotNull PersistentDataType dataType){
        ItemUtils.setPDCKey(itemStack, key, value, dataType);
        return this;
    }

    public ItemBuilder setUseCooldown(float cooldownInSeconds, @Nullable NamespacedKey cooldownGroup){
        ItemUtils.setUseCooldown(itemStack, cooldownInSeconds, cooldownGroup);
        return this;
    }

    public ItemBuilder setUseCooldown(float cooldownInSeconds, @Nullable String cooldownGroupKey){
        if (cooldownGroupKey == null){
            setUseCooldown(cooldownInSeconds, (NamespacedKey) null);
        }
        else{
            setUseCooldown(cooldownInSeconds, new NamespacedKey(CoreAPI.getPlugin(), cooldownGroupKey));
        }
        return this;
    }

    public ItemBuilder addCanPlace(@NotNull BlockType blockType, boolean showInToolTip){
        ItemUtils.addCanPlace(itemStack, List.of(blockType), showInToolTip);
        return this;
    }

    public ItemBuilder addCanPlace(@NotNull Collection<BlockType> blockTypes, boolean showInToolTip){
        ItemUtils.addCanPlace(itemStack, blockTypes, showInToolTip);
        return this;
    }

    public ItemBuilder addCanBreak(@NotNull BlockType blockType, boolean showInToolTip){
        ItemUtils.addCanBreak(itemStack, List.of(blockType), showInToolTip);
        return this;
    }

    public ItemBuilder addCanBreak(@NotNull Collection<BlockType> blockTypes, boolean showInToolTip){
        ItemUtils.addCanBreak(itemStack, blockTypes, showInToolTip);
        return this;
    }

    public ItemBuilder setEnchantmentGlintOverride(boolean override){
        ItemUtils.setEnchantmentGlintOverride(itemStack, override);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level){
        itemStack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder setMaxDamage(int maxDamage){
        ItemUtils.setMaxDamage(itemStack, maxDamage);
        return this;
    }

    public ItemBuilder setDamage(int damage){
        ItemUtils.setDamage(itemStack, damage);
        return this;
    }

    public ItemBuilder setPotionColor(Color color){
        itemStack.editMeta(PotionMeta.class, meta -> {
            meta.setColor(color);
        });
        return this;
    }

    public ItemBuilder setConsumable(@NotNull ItemAnimation animation, float consumeSeconds, boolean showParticles, @NotNull Sound sound){
        ItemUtils.setConsumable(itemStack, animation, consumeSeconds, showParticles, sound);
        return this;
    }

    public ItemBuilder setUseRemainder(@NotNull ItemStack item, ItemStack newItem){
        ItemUtils.setUseRemainder(item, newItem);
        return this;
    }

    public ItemBuilder setItemModel(@NotNull Material material){
        ItemUtils.setItemModel(itemStack, material);
        return this;
    }

    public ItemBuilder setGlider(boolean canGlide){
        ItemUtils.setGlider(itemStack, canGlide);
        return this;
    }

    public ItemBuilder setBasePotionType(PotionType potionType){
        if (!(itemStack.getItemMeta() instanceof PotionMeta)){
            return this;
        }
        itemStack.editMeta(PotionMeta.class, meta -> {
            meta.setBasePotionType(potionType);
        });
        return this;
    }

    public ItemBuilder addCustomPotionEffect(PotionEffect effect, boolean overwrite){
        if (!(itemStack.getItemMeta() instanceof PotionMeta)){
            return this;
        }
        itemStack.editMeta(PotionMeta.class, meta -> {
            meta.addCustomEffect(effect, overwrite);
        });
        return this;
    }

    public ItemBuilder setLeatherArmorColor(Color color){
        if (!(itemStack.getItemMeta() instanceof LeatherArmorMeta)){
            return this;
        }
        itemStack.editMeta(LeatherArmorMeta.class, meta -> {
            meta.setColor(color);
        });
        return this;
    }

    public ItemBuilder setLore(@Nullable List<Component> lore){
        itemStack.editMeta(meta -> {
            List<Component> fixedLore = new ArrayList<>();
            if (lore != null){

                for (Component c : lore){
                    fixedLore.add(c.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                }
                meta.lore(fixedLore);
            }
            else {
                meta.lore(null);
            }

        });
        return this;
    }

    public ItemBuilder addItemFlags(ItemFlag... itemFlags){
        itemStack.editMeta(meta -> {
            for (ItemFlag flag : itemFlags){
            //Attribute Required on an item to use Hide Attributes
                if (flag == ItemFlag.HIDE_ATTRIBUTES && !meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)){
                    AttributeModifier attributeModifier = CoreAPI.getVersionHandler().createAttributeModifier("hide_attr_flag", 0, AttributeModifier.Operation.ADD_NUMBER);
                    meta.addAttributeModifier(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS, attributeModifier);
                }
                meta.addItemFlags(flag);
            }
        });
        return this;
    }

    public ItemBuilder addLoreLine(Component line){
        itemStack.editMeta(meta -> {
            List<Component> compList;
            if (meta.hasLore()){
                compList = meta.lore();
            }
            else{
                compList = new ArrayList<>();
            }
            compList.add(line.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
            meta.lore(compList);
        });
        return this;
    }


    public ItemStack build(){
        return itemStack.clone();
    }

    @Override
    public ItemBuilder clone(){
        return new ItemBuilder(itemStack);
    }
}
