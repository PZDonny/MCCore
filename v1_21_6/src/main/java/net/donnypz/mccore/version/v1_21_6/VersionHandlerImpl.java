package net.donnypz.mccore.version.v1_21_6;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.donnypz.mccore.version.CoreAPI;
import net.donnypz.mccore.version.VersionHandler;
import org.bukkit.*;
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

public class VersionHandlerImpl implements VersionHandler {
    static {
        DEBUFF_EFFECTS.add(PotionEffectType.POISON);
        DEBUFF_EFFECTS.add(PotionEffectType.INSTANT_DAMAGE);
        DEBUFF_EFFECTS.add(PotionEffectType.WITHER);
        DEBUFF_EFFECTS.add(PotionEffectType.WEAKNESS);
        DEBUFF_EFFECTS.add(PotionEffectType.SLOWNESS);
        DEBUFF_EFFECTS.add(PotionEffectType.MINING_FATIGUE);
        DEBUFF_EFFECTS.add(PotionEffectType.BLINDNESS);
        DEBUFF_EFFECTS.add(PotionEffectType.DARKNESS);
        DEBUFF_EFFECTS.add(PotionEffectType.NAUSEA);
        DEBUFF_EFFECTS.add(PotionEffectType.UNLUCK);
        DEBUFF_EFFECTS.add(PotionEffectType.HUNGER);
        DEBUFF_EFFECTS.add(PotionEffectType.BAD_OMEN);
    }

    @Override
    public void useMinigameGamerules(@NotNull World world) {
        world.setGameRule(GameRule.DISABLE_RAIDS, true);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setGameRule(GameRule.DO_INSOMNIA, false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.MOB_GRIEFING, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
        world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
        world.setGameRule(GameRule.SPAWN_CHUNK_RADIUS, 0);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.setGameRule(GameRule.FORGIVE_DEAD_PLAYERS, true);
        world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
        world.setGameRule(GameRule.ENDER_PEARLS_VANISH_ON_DEATH, true);
        world.setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE, 101);
        world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
        world.setDifficulty(Difficulty.NORMAL);
    }

    @Override
    public void showScoreboardNumbers(@NotNull Objective objective) {
        objective.numberFormat(null);
    }

    @Override
    public void hideScoreboardNumbers(@NotNull Objective objective) {
        objective.numberFormat(NumberFormat.blank());
    }

    @Override
    public Particle getDustParticle() {
        return Particle.DUST;
    }

    @Override
    public Particle getBlockParticle() {
        return Particle.BLOCK;
    }

    @Override
    public Attribute getMaxHealthAttribute() {
        return Attribute.MAX_HEALTH;
    }

    @Override
    public void setRespawnLocation(@NotNull Player player, @Nullable Location location, boolean force) {
        player.setRespawnLocation(location, force);
    }

    @Override
    public void hideAdditionalTooltip(@NotNull ItemStack itemStack) {}

    @Override
    public void hideBannerPattern(@NotNull ItemStack itemStack) {
        TooltipDisplay.Builder builder = TooltipDisplay.tooltipDisplay();
        TooltipDisplay hidden = itemStack.getData(DataComponentTypes.TOOLTIP_DISPLAY);
        HashSet<DataComponentType> types;
        if (hidden == null){
            types = new HashSet<>();
        }
        else{
            types = new HashSet<>(hidden.hiddenComponents());
        }
        types.add(DataComponentTypes.BANNER_PATTERNS);
        itemStack.setData(DataComponentTypes.TOOLTIP_DISPLAY, builder.hiddenComponents(types).build());
    }

    @Override
    public PatternType getRhombusPattern() {
        return PatternType.RHOMBUS;
    }

    @Override
    public AttributeModifier createAttributeModifier(@NotNull String key, double amount, AttributeModifier.Operation operation) {
        return new AttributeModifier(new NamespacedKey(CoreAPI.getPlugin(), key), amount, operation);
    }
}
