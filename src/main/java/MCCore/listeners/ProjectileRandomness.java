package MCCore.listeners;

import MCCore.Core;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

public class ProjectileRandomness implements Listener {


    @EventHandler (priority = EventPriority.HIGHEST)
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        if (!Core.projectileRandomness){
            return;
        }

        //Makes projectiles shoot simillar to 1.8 instead of being affected by player velocity
        //Arrows shot from a bow and non-Multishot crossbows will shoot more naturally
        //Multishot crossbows shoot arrows w/o the effects of player velocity, but with nearly the same speed and directtion as newer versions

        Projectile projectile = e.getEntity();
        ProjectileSource shooter = projectile.getShooter();

        if (shooter instanceof Player p) {
            Vector pVec = p.getEyeLocation().getDirection().normalize();
            Vector projectileDirection = projectile.getVelocity();
            double prevSpeed = projectileDirection.length();

        //Crossbow
            if (p.getInventory().getItemInMainHand().containsEnchantment(Enchantment.MULTISHOT)
                    || (p.getInventory().getItemInOffHand().containsEnchantment(Enchantment.MULTISHOT)
                    && !p.getInventory().getItemInMainHand().getType().equals(Material.BOW)
                    && !p.getInventory().getItemInMainHand().getType().equals(Material.CROSSBOW))){
                pVec.setX(projectileDirection.getX());
                pVec.setZ(projectileDirection.getZ());

                //Set to 1 because crossbow's with multishot shot with their previous speed (projectileDirection.length)
                //end up shooting too fast
                //prevSpeed = 1;

                //Multiply without changing the Y first to fix crossbow incorrections in pitch direction
                //pVec.multiply(prevSpeed);
                pVec.setY(projectile.getVelocity().getY());
            }
            else{
                pVec.multiply(prevSpeed);
            }

            projectile.setVelocity(pVec);
        }
    }
}
