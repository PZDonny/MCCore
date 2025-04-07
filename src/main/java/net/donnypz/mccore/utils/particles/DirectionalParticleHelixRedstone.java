package net.donnypz.mccore.utils.particles;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.*;

public class DirectionalParticleHelixRedstone extends ParticleHelix{
    float particleSize = 1;
    Color[] colors;


    public DirectionalParticleHelixRedstone(double radius, double maxRevolutions, double forwardStep){
        super(radius, maxRevolutions, forwardStep);
        this.colors = new Color[]{Color.RED};
    }

    public DirectionalParticleHelixRedstone setParticleSize(float particleSize){
        this.particleSize = particleSize;
        return this;
    }


    public DirectionalParticleHelixRedstone setColors(Color... colors){
        if (colors == null || colors.length == 0){
            return this;
        }
        this.colors = colors;
        return this;
    }

    public float getParticleSize() {
        return particleSize;
    }

    @Override
    protected void spawnParticle(Location location) {
        Color color = colors[random.nextInt(colors.length)];
        location.getWorld().spawnParticle(Particle.DUST, location, amount, 0,0,0, new Particle.DustOptions(color, particleSize));
    }

    @Override
    protected void spawnParticle(Collection<Player> players, Location location) {
        Color color = colors[random.nextInt(colors.length)];
        for (Player player : players){
            if (!player.isOnline()){
                continue;
            }
            player.getWorld().spawnParticle(Particle.DUST, location, amount, 0,0,0, new Particle.DustOptions(color, particleSize));
        }
    }
}
