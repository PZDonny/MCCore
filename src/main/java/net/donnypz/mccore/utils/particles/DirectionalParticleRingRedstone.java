package net.donnypz.mccore.utils.particles;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.*;

public class DirectionalParticleRingRedstone extends ParticleRing{
    Color[] colors;
    float particleSize = 1;

    public DirectionalParticleRingRedstone(double radius, double angleToNext){
        super(radius, angleToNext);
        this.colors = new Color[]{Color.RED};
    }

    public DirectionalParticleRingRedstone setColors(Color... colors){
        if (colors == null || colors.length == 0){
            return this;
        }
        this.colors = colors;
        return this;
    }

    public DirectionalParticleRingRedstone setParticleSize(float size){
        this.particleSize = Math.abs(size);
        return this;
    }

    public float getParticleSize() {
        return particleSize;
    }

    protected void spawnParticle(Location location){
        Color color = colors[(random.nextInt(colors.length))];
        location.getWorld().spawnParticle(Particle.DUST, location, amount, 0,0,0, new Particle.DustOptions(color, particleSize));
    }

    protected void spawnParticle(Collection<Player> players, Location location){
        Color color = colors[(random.nextInt(colors.length))];
        for (Player player : players){
            if (!player.isOnline()){
                continue;
            }
            player.spawnParticle(Particle.DUST, location, amount, 0,0,0, new Particle.DustOptions(color, particleSize));
        }
    }
}
