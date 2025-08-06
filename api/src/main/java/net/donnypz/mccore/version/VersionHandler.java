package net.donnypz.mccore.version;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Objective;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public interface VersionHandler {
    Set<PotionEffectType> DEBUFF_EFFECTS = new HashSet<>();

    void useMinigameGamerules(@NotNull World world);

    void showScoreboardNumbers(@NotNull Objective objective);

    void hideScoreboardNumbers(@NotNull Objective objective);

    Particle getDustParticle();

    Particle getBlockParticle();

    Attribute getMaxHealthAttribute();

    void setRespawnLocation(@NotNull Player player, @Nullable Location location, boolean force);

    void hideAdditionalTooltip(@NotNull ItemStack itemStack);

    void hideBannerPattern(@NotNull ItemStack itemStack);

    PatternType getRhombusPattern();

    AttributeModifier createAttributeModifier(@NotNull String key, double amount, AttributeModifier.Operation operation);

    default boolean isDebuffPotionEffect(@NotNull PotionEffectType potionEffectType){
        return DEBUFF_EFFECTS.contains(potionEffectType);
    }
}
