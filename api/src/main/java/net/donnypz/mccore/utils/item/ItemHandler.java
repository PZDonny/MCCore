package net.donnypz.mccore.utils.item;

import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.BlockType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface ItemHandler {
    ItemStack setEnchantmentGlintOverride(@NotNull ItemStack item, boolean override);

    ItemStack setUseRemainder(@NotNull ItemStack item, ItemStack newItem);

    ItemStack setItemModel(@NotNull ItemStack item, @NotNull Material material);

    ItemStack setGlider(@NotNull ItemStack item, boolean canGlide);

    ItemStack setMaxStackSize(@NotNull ItemStack item, int maxStackSize);

    ItemStack setDamage(@NotNull ItemStack item, int damage);

    ItemStack setMaxDamage(@NotNull ItemStack item, int maxDamage);

    void setTooltipHidden(@NotNull ItemStack item, boolean hidden);

    ItemStack setUseCooldown(@NotNull ItemStack item, float cooldownInSeconds, @Nullable NamespacedKey cooldownGroup);

    ItemStack setConsumable(@NotNull ItemStack item, @NotNull ItemAnimation animation, float consumeSeconds, boolean showParticles, @NotNull Key sound);

    ItemStack randomizeUseCooldownGroup(@NotNull ItemStack item);

    ItemStack randomizeUseCooldownGroup(@NotNull ItemStack item, @NotNull String namespace);

    boolean hasFoodOrIsEdible(@NotNull ItemStack item);

    ItemStack addCanPlace(@NotNull ItemStack item, @NotNull Collection<BlockType> blockType, boolean showInTooltip);

    ItemStack addCanBreak(@NotNull ItemStack item, @NotNull Collection<BlockType> blockType, boolean showInTooltip);
}
