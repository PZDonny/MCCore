package net.donnypz.mccore.utils.particles;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class EntityParticleRotationRedstone extends ParticleRotation{
    private Color[] colors;
    private float particleSize = 1;

    public EntityParticleRotationRedstone(@NotNull Entity entity, double radius){
        super(entity, radius);
        this.colors = new Color[]{Color.RED};
    }

    public EntityParticleRotationRedstone setColors(Color... colors){
        if (colors == null || colors.length == 0){
            return this;
        }
        this.colors = colors;
        return this;
    }

    public EntityParticleRotationRedstone setParticleSize(int particleSize){
        this.particleSize = particleSize;
        return this;
    }


    protected void spawnParticle(Location location){
        Color color = colors[(random.nextInt(colors.length))];
        location.getWorld().spawnParticle(Particle.DUST, location, amount, 0,0,0, new Particle.DustOptions(color, particleSize));
    }
}
