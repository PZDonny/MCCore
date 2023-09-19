package MCCore.utils.ParticleShapes;

import MCCore.Core;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DirectionalParticleHelix {

    Location location;
    double radius;

    double forwardSpacing;

    double maxRevolutions;

    double extra;

    int amount = 1;

    int ticks = 1;

    double angle = 15;
    boolean clockwise = true;

    Random random = new Random();

    List<Particle> particles = new ArrayList<>();


    public DirectionalParticleHelix(Location location, double radius, double forwardSpacing, double maxRevolutions, double extra){
        this.location = location.clone();
        this.radius = radius;
        this.forwardSpacing = forwardSpacing;
        this.maxRevolutions = maxRevolutions;
        this.extra = extra;
        this.particles.add(Particle.FLAME);
    }

    public DirectionalParticleHelix setParticleAmount(int amount){
        this.amount = amount;
        return this;
    }

    public DirectionalParticleHelix setTicksBetweenRotation(int ticks){
        if (ticks <= 0) ticks = 1;
        this.ticks = ticks;
        return this;
    }


    public DirectionalParticleHelix setParticles(Particle... particles){
        if (particles == null || particles.length == 0) return this;
        this.particles.clear();
        this.particles = Arrays.asList(particles);
        return this;
    }

    public DirectionalParticleHelix setAngleToNextParticle(double angle){
        this.angle = angle;
        return this;
    }

    public DirectionalParticleHelix setRotateClockwise(boolean clockwise){
        this.clockwise = clockwise;
        return this;
    }

    public DirectionalParticleHelix setLocation(Location loc){
        this.location = loc.clone();
        return this;
    }


    public void spawn(){

        Location loc = location.clone();
        Location offset = location.clone();
        offset.setPitch(location.getPitch()+90F);

        Vector extendVector = loc.getDirection().multiply(forwardSpacing);
        Vector offsetVector = offset.getDirection().normalize().multiply(radius);

        new BukkitRunnable(){
            double currentAngle = 0;
            public void run(){
                Location particleLoc;
                if (clockwise) particleLoc = loc.clone().add(offsetVector.rotateAroundAxis(extendVector, Math.toRadians(angle)).normalize().multiply(radius));
                else particleLoc = loc.clone().add(offsetVector.rotateAroundAxis(extendVector, Math.toRadians(angle*-1)).normalize().multiply(radius));

                Particle particle = particles.get(random.nextInt(particles.size()));
                particleLoc.getWorld().spawnParticle(particle, particleLoc, amount, 0,0,0, extra);
                loc.add(extendVector);

                currentAngle+=angle;
                if (currentAngle > maxRevolutions*360 && maxRevolutions != 0){
                    cancel();
                }
            }

        }.runTaskTimer(Core.getInstance(),0, ticks);
    }


}
