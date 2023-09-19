package MCCore.cosmetics;

import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Trident;

import java.util.LinkedHashMap;

public abstract class ProjectileTrail extends Cosmetic{

    private static final LinkedHashMap<String, ProjectileTrail> allProjectileTrails = new LinkedHashMap<>();

    public ProjectileTrail(String trailName){
        super(trailName);
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
