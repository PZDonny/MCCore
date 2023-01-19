package MCCore.utils;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

public class ParticleTools {

//Draw Particle Line To/From Locations
    public static void drawParticleLine(Location l1, Location l2, Vector v, Particle particle, int particleAmnt) {
        for(double i = 0.5; i<l1.distance(l2); i+= 0.25) {
            Vector vector = new Vector().copy(v).multiply(i);
            Location loc = l1.clone().add(vector);
            loc.getWorld().spawnParticle(particle, loc, particleAmnt);
        }
    }

//Draw Particle Line To/From Locations WITH Offsets
    public static void drawParticleLine(Location l1, Location l2, Vector v, Particle particle, int particleAmnt, double x, double y, double z) {
        for(double i = 0.5; i<l1.distance(l2); i+= 0.25) {
            Vector vector = new Vector().copy(v).multiply(i);
            Location loc = l1.clone().add(vector);
            loc.getWorld().spawnParticle(particle, loc, particleAmnt, x, y, z);
        }
    }


//Draw Redstone Particle Line To/From Locations
    public static void drawRedstoneLine(Location l1, Location l2, Vector v, Color color, int particleAmnt, float size) {
        for(double i = 0.5; i<l1.distance(l2); i+= 0.25) {
            Vector vector = new Vector().copy(v).multiply(i);
            Location loc = l1.clone().add(vector);
            loc.getWorld().spawnParticle(Particle.REDSTONE, loc, particleAmnt, 0.1, 0.1, 0.1, new Particle.DustOptions(color, size));
        }
    }

//Draw Redstone Particle Line To/From Locations WITH Offsets
    public static void drawRedstoneLine(Location l1, Location l2, Vector v, Color color, int particleAmnt, float size, double x, double y, double z) {
        for(double i = 0.5; i<l1.distance(l2); i+= 0.25) {
            Vector vector = new Vector().copy(v).multiply(i);
            Location loc = l1.clone().add(vector);
            loc.getWorld().spawnParticle(Particle.REDSTONE, loc, particleAmnt, x, y, z, new Particle.DustOptions(color, size));
        }
    }
}
