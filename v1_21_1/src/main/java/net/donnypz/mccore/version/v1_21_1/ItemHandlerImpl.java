package net.donnypz.mccore.version.v1_21_1;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistrySet;
import net.donnypz.mccore.utils.item.ItemAnimation;
import net.donnypz.mccore.utils.item.ItemHandler;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.BlockType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ItemHandlerImpl implements ItemHandler {

    @Override
    public ItemStack setEnchantmentGlintOverride(@NotNull ItemStack item, boolean override) {
        item.editMeta(meta ->{
            meta.setEnchantmentGlintOverride(override);
        });
        return item;
    }

    @Override
    public ItemStack setUseRemainder(@NotNull ItemStack item, ItemStack newItem) {
        return item;
    }

    @Override
    public ItemStack setItemModel(@NotNull ItemStack item, @NotNull Material material) {
        return item;
    }

    @Override
    public ItemStack setGlider(@NotNull ItemStack item, boolean canGlide) {
        return item;
    }

    @Override
    public ItemStack setMaxStackSize(@NotNull ItemStack item, int maxStackSize) {
        item.editMeta(meta ->{
            meta.setMaxStackSize(maxStackSize);
        });
        return item;
    }

    @Override
    public ItemStack setDamage(@NotNull ItemStack item, int damage) {
        item.editMeta(Damageable.class, meta -> {
            meta.setDamage(damage);
        });
        return item;
    }

    @Override
    public ItemStack setMaxDamage(@NotNull ItemStack item, int maxDamage) {
        item.editMeta(Damageable.class, meta -> {
            meta.setMaxDamage(maxDamage);
        });
        return item;
    }

    @Override
    public void setTooltipHidden(@NotNull ItemStack item, boolean hidden){
        item.editMeta(meta -> {
            meta.setHideTooltip(hidden);
        });
    }

    @Override
    public ItemStack setUseCooldown(@NotNull ItemStack item, float cooldownInSeconds, @Nullable NamespacedKey cooldownGroup){
        return item;
    }

    @Override
    public ItemStack setConsumable(@NotNull ItemStack item, @NotNull ItemAnimation animation, float consumeSeconds, boolean showParticles, @NotNull Key sound) {
        return item;
    }

    @Override
    public ItemStack randomizeUseCooldownGroup(@NotNull ItemStack item) {
        return item;
    }

    @Override
    public ItemStack randomizeUseCooldownGroup(@NotNull ItemStack item, @NotNull String namespace){
        return item;
    }

    @Override
    public boolean hasFoodOrIsEdible(@NotNull ItemStack item) {
        return (item.hasItemMeta() && item.getItemMeta().hasFood())
                || item.getType().isEdible();
    }

    @Override
    public ItemStack addCanPlace(@NotNull ItemStack item, @NotNull Collection<BlockType> blockTypes, boolean showInTooltip) {
        item.editMeta(meta -> {
            Set<Material> blocks = new HashSet<>(meta.getCanPlaceOn());
            for (BlockType blockType : blockTypes){
                blocks.add(Registry.MATERIAL.get(NamespacedKey.minecraft(blockType.key().asMinimalString())));
            }
            meta.setCanPlaceOn(blocks);
            meta.setHideTooltip(!showInTooltip);
        });
        return item;
    }

    @Override
    public ItemStack addCanBreak(@NotNull ItemStack item, @NotNull Collection<BlockType> blockTypes, boolean showInTooltip) {
        item.editMeta(meta -> {
            Set<Material> blocks = new HashSet<>(meta.getCanDestroy());
            for (BlockType blockType : blockTypes){
                blocks.add(Registry.MATERIAL.get(NamespacedKey.minecraft(blockType.key().asMinimalString())));
            }
            meta.setCanDestroy(blocks);
            meta.setHideTooltip(!showInTooltip);
        });
        return item;
    }
}
