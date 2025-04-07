package net.donnypz.mccore.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;

public class EntityUtils {

    public static boolean isNPC(@NotNull Entity entity){
        return entity.hasMetadata("NPC");
    }

    public static void setPDCValue(@NotNull Entity entity, @NotNull NamespacedKey key, @NotNull Object value, @NotNull PersistentDataType dataType){
        entity.getPersistentDataContainer().set(key, dataType, value);
    }

    public static void unsetPDCValue(@NotNull Entity entity, @NotNull NamespacedKey key){
        entity.getPersistentDataContainer().remove(key);
    }

    public static <P, C> C getPDCValue(@NotNull Entity entity, @NotNull NamespacedKey key, @NotNull PersistentDataType<P, C> dataType){
        Object obj = entity.getPersistentDataContainer().get(key, dataType);
        return dataType.getComplexType().cast(obj);
    }

    public static boolean hasPDCKey(@NotNull Entity entity, @NotNull NamespacedKey key, @NotNull PersistentDataType dataType){
        return entity.getPersistentDataContainer().has(key, dataType);
    }

    public static void removeAttributeModifiers(LivingEntity entity){
        for (Attribute att : Registry.ATTRIBUTE) {
            AttributeInstance instance = entity.getAttribute(att);
            if (instance == null) {
                continue;
            }
            for (AttributeModifier mod : instance.getModifiers()) {
                instance.removeModifier(mod);
            }
        }
    }


    public static void removePassengers(Entity vehicle){
        if (vehicle == null || vehicle.isDead() || vehicle.isEmpty()){
            return;
        }
        for (Entity passenger : new ArrayList<>(vehicle.getPassengers())){
            removePassengers(passenger);
            vehicle.removePassenger(passenger);
            if (!(passenger instanceof Player)){
                passenger.remove();
            }
        }
        vehicle.remove();
    }
}
