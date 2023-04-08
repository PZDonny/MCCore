package MCCore.utils.ParticleShapes;

import MCCore.Core;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DirectionalParticleSpiralRedstone {

    Location location;
    double radius;

    double forwardSpacing;

    double maxLength;

    int amount = 1;

    int ticks = 1;

    double angle = 15;
    boolean clockwise = true;

    float particleSize = 1;

    Random random = new Random();

    List<Color> colors = new ArrayList<>();


    public DirectionalParticleSpiralRedstone(Location location, Color color, double radius, double forwardSpacing, double maxLength){
        this.location = location.clone();
        this.radius = radius;
        this.forwardSpacing = forwardSpacing;
        this.maxLength = maxLength;
        this.colors.add(color);
    }

    public DirectionalParticleSpiralRedstone(Location location, List<Color> colors, double radius, double forwardSpacing, double maxLength){
        this.location = location.clone();
        this.radius = radius;
        this.forwardSpacing = forwardSpacing;
        this.maxLength = maxLength;
        this.colors = colors;
    }

    public void setAmountPerRotation(int amount){
        this.amount = amount;
    }

    public void setTicksBetweenRotation(int ticks){
        if (ticks < 0) ticks = 0;
        this.ticks = ticks;
    }

    public void setParticleSize(float size){
        this.particleSize = size;
    }


    public void setAngleToNextParticle(double angle){
        this.angle = angle;
    }

    public void setRotateClockwise(boolean clockwise){
        this.clockwise = clockwise;
    }

    public void setLocation(Location loc){
        this.location = loc.clone();
    }


    public void spawn(){
        Location loc = location.clone();
        float pitch = loc.getPitch();
        loc.setPitch(pitch);
        Vector originalVec = loc.getDirection().clone();

        originalVec.normalize();
        Vector v = originalVec.clone();
        v.setX(radius);
        v.setZ(radius);
        v.setY(radius);

        Vector extendVector = originalVec.clone();
        extendVector.multiply(forwardSpacing);


        new BukkitRunnable(){
            double currentLength = 0;
            public void run(){
                Location particleLoc = loc.clone();
                particleLoc.setPitch(pitch);

                Vector addVec;
                if (clockwise) addVec = v.rotateAroundAxis(extendVector, Math.toRadians(angle));
                else addVec = v.rotateAroundAxis(extendVector, Math.toRadians(angle*-1));
                particleLoc.add(addVec);

                Color color = colors.get(random.nextInt(colors.size()));
                particleLoc.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, amount, 0,0,0, new Particle.DustOptions(color, particleSize));

                loc.add(extendVector);
                currentLength+=forwardSpacing;
                if (currentLength >= maxLength) cancel();
            }
        }.runTaskTimer(Core.getInstance(), 0, ticks);

    }


}
