package net.donnypz.mccore.cosmetics.basic;

import net.donnypz.mccore.cosmetics.Cosmetic;
import net.donnypz.mccore.cosmetics.CosmeticRegistry;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Projectile;

import java.util.LinkedHashMap;

public abstract class ProjectileTrail extends Cosmetic {

    private static final LinkedHashMap<String, ProjectileTrail> allProjectileTrails = new LinkedHashMap<>();

    public ProjectileTrail(String cosmeticName, CosmeticRegistry registry){
        super(cosmeticName, registry);
    }

    public abstract void execute(Projectile projectile);

    public boolean isProjectileEligible(Projectile projectile){
        if (projectile.getTicksLived() > 20*15){ //15s of effects
            return false;
        }
        if (projectile.isDead()){
            return false;
        }
        if ((projectile instanceof AbstractArrow && ((AbstractArrow) projectile).isInBlock())){
            return false;
        }

        return true;
    }
}
