package net.donnypz.mccore.utils.particles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.*;

public class DirectionalParticleHelix extends ParticleHelix{
    double extra;
    Particle[] particles;


    public DirectionalParticleHelix(double radius, double maxRevolutions, double extra, double forwardStep){
        super(radius, maxRevolutions, forwardStep);
        this.extra = extra;
        this.particles = new Particle[]{Particle.FLAME};
    }

    public ParticleHelix setParticles(Particle... particles){
        if (particles == null || particles.length == 0){
            return this;
        }
        this.particles = particles;
        return this;
    }


    @Override
    protected void spawnParticle(Location location) {
        Particle particle = particles[random.nextInt(particles.length)];
        location.getWorld().spawnParticle(particle, location, amount, 0,0,0, extra);
    }

    @Override
    protected void spawnParticle(Collection<Player> players, Location location) {
        Particle particle = particles[random.nextInt(particles.length)];
        for (Player player : players){
            if (!player.isOnline()){
                continue;
            }
            player.spawnParticle(particle, location, amount, 0,0,0, extra);
        }
    }
}
