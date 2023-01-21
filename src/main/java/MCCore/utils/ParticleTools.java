package MCCore.utils;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

public class ParticleTools {

//Draw Particle Line To/From Locations
    public static void drawParticleLine(Location from, Location to, Particle particle, int particleAmnt, double extra) {
        for(double i = 0.5; i<from.distance(to); i+= 0.25) {
            Vector vector  = from.clone().toVector().subtract(to.toVector()).normalize().multiply(-i);
            Location loc = from.clone().add(vector);
            loc.getWorld().spawnParticle(particle, loc, particleAmnt, 0, 0, 0, extra);
        }
    }

//Draw Particle Line To/From Locations WITH Offsets
    public static void drawParticleLine(Location from, Location to, Particle particle, int particleAmnt, double x, double y, double z, double extra) {
        for(double i = 0.5; i<from.distance(to); i+= 0.25) {
            Vector vector  = from.clone().toVector().subtract(to.toVector()).normalize().multiply(-i);
            Location loc = from.clone().add(vector);
            loc.getWorld().spawnParticle(particle, loc, particleAmnt, x, y, z, extra);
        }
    }


//Draw Redstone Particle Line To/From Locations
    public static void drawRedstoneLine(Location from, Location to, Color color, int particleAmnt, float size) {
        for(double i = 0.5; i<from.distance(to); i+= 0.25) {
            Vector vector  = from.clone().toVector().subtract(to.toVector()).normalize().multiply(-i);
            Location loc = from.clone().add(vector);
            loc.getWorld().spawnParticle(Particle.REDSTONE, loc, particleAmnt, 0.1, 0.1, 0.1, new Particle.DustOptions(color, size));
        }
    }

//Draw Redstone Particle Line To/From Locations WITH Offsets
    public static void drawRedstoneLine(Location from, Location to, Color color, int particleAmnt, float size, double x, double y, double z) {
        for(double i = 0.5; i<from.distance(to); i+= 0.25) {
            Vector vector  = from.clone().toVector().subtract(to.toVector()).normalize().multiply(-i);
            Location loc = from.clone().add(vector);
            loc.getWorld().spawnParticle(Particle.REDSTONE, loc, particleAmnt, x, y, z, new Particle.DustOptions(color, size));
        }
    }
}
