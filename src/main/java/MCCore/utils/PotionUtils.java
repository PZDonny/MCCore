package MCCore.utils;

import org.bukkit.potion.PotionEffectType;

public class PotionUtils {
    public static boolean isDebuffPotionEffect(PotionEffectType potionEffectType){

        if (potionEffectType == PotionEffectType.POISON) {
            return true;
        }
        if (potionEffectType == PotionEffectType.HARM) {
            return true;
        }
        if (potionEffectType == PotionEffectType.WITHER) {
            return true;
        }
        if (potionEffectType == PotionEffectType.WEAKNESS) {
            return true;
        }
        if (potionEffectType == PotionEffectType.SLOW) {
            return true;
        }
        if (potionEffectType == PotionEffectType.SLOW_DIGGING) {
            return true;
        }
        if (potionEffectType == PotionEffectType.BLINDNESS) {
            return true;
        }
        if (potionEffectType == PotionEffectType.DARKNESS) {
            return true;
        }
        if (potionEffectType == PotionEffectType.CONFUSION) {
            return true;
        }
        if (potionEffectType == PotionEffectType.UNLUCK) {
            return true;
        }
        if (potionEffectType == PotionEffectType.HUNGER) {
            return true;
        }
        if (potionEffectType == PotionEffectType.BAD_OMEN) {
            return true;
        }
        return false;
    }
}
