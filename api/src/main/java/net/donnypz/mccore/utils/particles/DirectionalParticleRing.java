package net.donnypz.mccore.utils.particles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.*;

public class DirectionalParticleRing extends ParticleRing{
    Particle[] particles;
    double extra;

    public DirectionalParticleRing(double radius, double angleToNext, double extra){
        super(radius, angleToNext);
        this.extra = extra;
        this.particles = new Particle[]{Particle.FLAME};
    }

    public DirectionalParticleRing setParticles(Particle... particles){
        if (particles == null || particles.length == 0){
            return this;
        }
        this.particles = particles;
        return this;
    }

    protected void spawnParticle(Location location){
        Particle particle = particles[(random.nextInt(particles.length))];
        location.getWorld().spawnParticle(particle, location, amount, 0,0,0, extra);
    }

    protected void spawnParticle(Collection<Player> players, Location location){
        Particle particle = particles[(random.nextInt(particles.length))];
        for (Player player : players){
            if (!player.isOnline()){
                continue;
            }
            player.spawnParticle(particle, location, amount, 0,0,0, extra);
        }
    }
}
