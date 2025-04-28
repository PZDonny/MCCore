package net.donnypz.mccore.utils.misc;

import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PotionUtils {

    private static final Set<PotionEffectType> DEBUFF_EFFECTS;

    static {
        Set<PotionEffectType> debuffs = new HashSet<>();
        debuffs.add(PotionEffectType.POISON);
        debuffs.add(PotionEffectType.INSTANT_DAMAGE);
        debuffs.add(PotionEffectType.WITHER);
        debuffs.add(PotionEffectType.WEAKNESS);
        debuffs.add(PotionEffectType.SLOWNESS);
        debuffs.add(PotionEffectType.MINING_FATIGUE);
        debuffs.add(PotionEffectType.BLINDNESS);
        debuffs.add(PotionEffectType.DARKNESS);
        debuffs.add(PotionEffectType.NAUSEA);
        debuffs.add(PotionEffectType.UNLUCK);
        debuffs.add(PotionEffectType.HUNGER);
        debuffs.add(PotionEffectType.BAD_OMEN);

        DEBUFF_EFFECTS = Collections.unmodifiableSet(debuffs);
    }

    /**
     * Check if the given {@link PotionEffectType} is a debuff.
     *
     * @param potionEffectType The potion effect type to check.
     * @return true if the potion effect is a debuff
     */
    public static boolean isDebuffPotionEffect(@NotNull PotionEffectType potionEffectType) {
        return DEBUFF_EFFECTS.contains(potionEffectType);
    }
}
