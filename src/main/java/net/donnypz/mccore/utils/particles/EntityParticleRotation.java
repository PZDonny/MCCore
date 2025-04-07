package net.donnypz.mccore.utils.particles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class EntityParticleRotation extends ParticleRotation{
    private final double extra;
    private Particle[] particles;

    public EntityParticleRotation(@NotNull Entity entity, double radius, double extra){
        super(entity, radius);
        this.extra = extra;
        this.particles = new Particle[]{Particle.FLAME};
    }

    public EntityParticleRotation setParticles(Particle... particles){
        if (particles == null || particles.length == 0) {
            return this;
        }
        this.particles = particles;
        return this;
    }


    protected void spawnParticle(Location location){
        Particle particle = particles[(random.nextInt(particles.length))];
        location.getWorld().spawnParticle(particle, location, amount, 0,0,0, extra);
    }
}
