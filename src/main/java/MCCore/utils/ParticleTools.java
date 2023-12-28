package MCCore.utils;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

public class ParticleTools {

//Draw Particle Line To/From Locations
    public static void drawParticleLine(Location from, Location to, Particle particle, int particleAmnt, double extra) {
        drawParticleLine(from, to, particle, particleAmnt, 0, 0, 0, extra);
    }

//Draw Particle Line To/From Locations WITH Offsets
    public static void drawParticleLine(Location from, Location to, Particle particle, int particleAmnt, double x, double y, double z, double extra) {
        double distance = from.distance(to);
        double speed = distance/20;
        if (speed == 0){
            return;
        }
        for(double i = speed; i<distance; i+= speed) {
            Vector vector  = from.clone().toVector().subtract(to.toVector()).normalize().multiply(-i);
            Location loc = from.clone().add(vector);
            loc.getWorld().spawnParticle(particle, loc, particleAmnt, x, y, z, extra);
        }
    }


//Draw Redstone Particle Line To/From Locations
    public static void drawRedstoneLine(Location from, Location to, Color color, int particleAmnt, float size) {
        drawRedstoneLine(from, to, color,particleAmnt, size, 0.1, 0.1, 0.1);
    }

//Draw Redstone Particle Line To/From Locatiexecuter.sendMessage(ChatColor.GREEN+"+"+ChatColor.DARK_AQUA+"Weighted Boots!");WITH Offsets
    public static void drawRedstoneLine(Location from, Location to, Color color, int particleAmnt, float size, double x, double y, double z) {
        double distance = from.distance(to);
        double speed = distance/20;
        if (speed == 0){
            return;
        }
        for(double i = speed; i<distance; i+= speed) {
            Vector vector  = from.clone().toVector().subtract(to.toVector()).normalize().multiply(-i);
            Location loc = from.clone().add(vector);
            loc.getWorld().spawnParticle(Particle.REDSTONE, loc, particleAmnt, x, y, z, new Particle.DustOptions(color, size));
        }
    }
}
