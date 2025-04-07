package net.donnypz.mccore.utils.particles;

import net.donnypz.mccore.Core;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.function.Consumer;

public class ParticleLine extends ParticleEmitter{
    double xOffset = 0;
    double yOffset = 0;
    double zOffset = 0;
    double particleSpacing = 0.1;
    double extra;
    Particle particle;
    int tickDelay = 0;
    Object data;


    public ParticleLine(Particle particle){
        this.particle = particle;
    }

    public ParticleLine xOffset(double xOffset) {
        this.xOffset = xOffset;
        return this;
    }

    public ParticleLine yOffset(double yOffset) {
        this.yOffset = yOffset;
        return this;
    }

    public ParticleLine zOffset(double zOffset) {
        this.zOffset = zOffset;
        return this;
    }

    public ParticleLine particleSpacing(double particleSpacing) {
        this.particleSpacing = particleSpacing;
        return this;
    }

    public ParticleLine extra(double extra) {
        this.extra = extra;
        return this;
    }

    public ParticleLine tickDelay(int tickDelay) {
        this.tickDelay = tickDelay;
        return this;
    }

    public ParticleLine setData(Object data){
        this.data = data;
        return this;
    }

    public void spawn(Location from, double distance, Consumer<Location> action){
        Vector vector = from.getDirection().normalize().multiply(distance);
        Location to = from.clone();
        to.add(vector);
        spawn(from, to, distance, action);
    }

    public void spawn(Location from, Location to, Consumer<Location> action){
        spawn(from, to, from.distance(to), action);
    }


    private void spawn(Location from, Location to, double distance, Consumer<Location> action){
        Vector vector  = from.clone().toVector().subtract(to.toVector()).normalize().multiply(-1);
        Location particleLoc = from.clone();
        if (tickDelay <= 0){
            for(double i = 0; i<distance; i+= particleSpacing) {
                particleLoc.add(vector);
                action.accept(particleLoc);
            }
        }
        else{
            int delay = this.tickDelay;
            new BukkitRunnable(){

                @Override
                public void run() {
                    particleLoc.add(vector);
                    spawnParticle(particleLoc);
                    action.accept(particleLoc);
                }
            }.runTaskTimer(Core.getInstance(), 0, delay);
        }
    }

    @Override
    protected void spawnParticle(Location particleLoc){
        particleLoc.getWorld().spawnParticle(particle, particleLoc, amount, xOffset, yOffset, zOffset, extra, data);
    }

    @Override
    protected void spawnParticle(Collection<Player> players, Location particleLoc) {
        for (Player p : players){
            p.spawnParticle(particle, particleLoc, amount, xOffset, yOffset, zOffset, extra, data);
        }

    }

}
