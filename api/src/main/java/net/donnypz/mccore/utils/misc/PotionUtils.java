package net.donnypz.mccore.utils.misc;

import net.donnypz.mccore.version.CoreAPI;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class PotionUtils {

    /**
     * Check if the given {@link PotionEffectType} is a debuff.
     *
     * @param potionEffectType The potion effect type to check.
     * @return true if the potion effect is a debuff
     */
    public static boolean isDebuffPotionEffect(@NotNull PotionEffectType potionEffectType) {
        return CoreAPI.getVersionHandler().isDebuffPotionEffect(potionEffectType);
    }
}
