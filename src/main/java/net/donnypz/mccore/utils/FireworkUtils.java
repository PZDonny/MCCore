package net.donnypz.mccore.utils;

import net.donnypz.mccore.Core;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataType;

public class FireworkUtils {
    private static final NamespacedKey damagelessKey = new NamespacedKey(Core.getInstance(), "fw_nodmg");


    public static Firework createFirework(Location location, int ticksTillDetonation, FireworkEffect effect){
        return createFirework(location, ticksTillDetonation, effect, false);
    }

    public static Firework createFirework(Location location, int ticksTillDetonation, FireworkEffect.Type type, Color[] initialColors, Color[] fadeColors, boolean flicker, boolean trail){
        return createFirework(location, ticksTillDetonation, type, initialColors, fadeColors, flicker, trail, false);
    }

    public static Firework createDamageAllowedFirework(Location location, int ticksTillDetonation, FireworkEffect effect){
        return createFirework(location, ticksTillDetonation, effect, true);
    }

    public static Firework createDamageAllowedFirework(Location location, int ticksTillDetonation, FireworkEffect.Type type, Color[] initialColors, Color[] fadeColors, boolean flicker, boolean trail){
        return createFirework(location, ticksTillDetonation, type, initialColors, fadeColors, flicker, trail, true);
    }

    private static Firework createFirework(Location location, int ticksTillDetonation, FireworkEffect effect, boolean damage){
        if (effect == null){
            return null;
        }
        Firework fw = location.getWorld().spawn(location, Firework.class, f -> {
            f.setItem(new ItemStack(Material.FIREWORK_ROCKET));
        });
        FireworkMeta meta = fw.getFireworkMeta();
        meta.addEffect(effect);
        fw.setFireworkMeta(meta);
        fw.setTicksToDetonate(Math.max(ticksTillDetonation, 1));
        fw.setTicksFlown(0);
        if (!damage){
            EntityUtils.setPDCValue(fw, damagelessKey, true, PersistentDataType.BOOLEAN);
        }

        return fw;
    }

    private static Firework createFirework(Location location, int ticksTillDetonation, FireworkEffect.Type type, Color[] initialColors, Color[] fadeColors, boolean flicker, boolean trail, boolean damage){
        FireworkEffect.Builder effect = FireworkEffect.builder()
                .with(type);
        if (initialColors != null){
            effect.withColor(initialColors);
        }
        if (fadeColors != null){
            effect.withFade(fadeColors);
        }
        if (flicker){
            effect.withFlicker();
        }
        if (trail){
            effect.withTrail();
        }
        return createFirework(location, ticksTillDetonation, effect.build(), damage);
    }

    public static boolean isDamageDisabled(Firework firework){
        return EntityUtils.hasPDCKey(firework, damagelessKey, PersistentDataType.BOOLEAN);
    }
}
