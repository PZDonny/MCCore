package MCCore.utils.ParticleShapes;

import MCCore.Core;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class DirectionalParticleHelixRedstone {

    Location location;
    double radius;

    double forwardSpacing;

    double maxRevolutions;


    int amount = 1;

    int ticks = 1;

    double angle = 15;
    boolean clockwise = true;

    Random random = new Random();

    List<Color> colors = new ArrayList<>();

    float particleSize = 1;


    public DirectionalParticleHelixRedstone(Location location, double radius, double forwardSpacing, double maxRevolutions){
        this.location = location.clone();
        this.radius = radius;
        this.forwardSpacing = forwardSpacing;
        this.maxRevolutions = maxRevolutions;
        this.colors.add(Color.RED);
    }

    public DirectionalParticleHelixRedstone setParticleAmount(int amount){
        this.amount = amount;
        return this;
    }

    public DirectionalParticleHelixRedstone setTicksBetweenRotation(int ticks){
        if (ticks <= 0) ticks = 1;
        this.ticks = ticks;
        return this;
    }

    public DirectionalParticleHelixRedstone setParticleSize(float particleSize){
        this.particleSize = particleSize;
        return this;
    }


    public DirectionalParticleHelixRedstone setColors(Color... colors){
        if (colors == null || colors.length == 0) return this;
        this.colors.clear();
        this.colors = Arrays.asList(colors);
        return this;
    }

    public DirectionalParticleHelixRedstone setAngleToNextParticle(double angle){
        this.angle = angle;
        return this;
    }

    public DirectionalParticleHelixRedstone setRotateClockwise(boolean clockwise){
        this.clockwise = clockwise;
        return this;
    }

    public DirectionalParticleHelixRedstone setLocation(Location loc){
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

                Color color = colors.get(random.nextInt(colors.size()));
                particleLoc.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, amount, 0,0,0, new Particle.DustOptions(color, particleSize));
                loc.add(extendVector);

                currentAngle+=angle;
                if (currentAngle > maxRevolutions*360 && maxRevolutions != 0){
                    cancel();
                }
            }
        }.runTaskTimer(Core.getInstance(),0, ticks);
    }

    public void spawn(Player player){
        if (!player.isOnline()) return;
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

                Color color = colors.get(random.nextInt(colors.size()));
                player.spawnParticle(Particle.REDSTONE, particleLoc, amount, 0,0,0, new Particle.DustOptions(color, particleSize));
                loc.add(extendVector);

                currentAngle+=angle;
                if (currentAngle > maxRevolutions*360 && maxRevolutions != 0){
                    cancel();
                }
            }
        }.runTaskTimer(Core.getInstance(),0, ticks);
    }

    public void spawn(Collection<Player> players){
        Location loc = location.clone();
        Location offset = location.clone();
        offset.setPitch(location.getPitch()+90F);

        Vector extendVector = loc.getDirection().multiply(forwardSpacing);
        Vector offsetVector = offset.getDirection().normalize().multiply(radius);

        new BukkitRunnable(){
            double currentAngle = 0;
            public void run(){
                Location particleLoc;
                if (clockwise){
                    particleLoc = loc.clone().add(offsetVector.rotateAroundAxis(extendVector, Math.toRadians(angle)).normalize().multiply(radius));
                }
                else{
                    particleLoc = loc.clone().add(offsetVector.rotateAroundAxis(extendVector, Math.toRadians(angle)).normalize().multiply(radius*-1));
                }

                Color color = colors.get(random.nextInt(colors.size()));
                for (Player player : players){
                    if (!player.isOnline()) continue;
                    player.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, amount, 0,0,0, new Particle.DustOptions(color, particleSize));
                }

                loc.add(extendVector);

                currentAngle+=angle;
                if (currentAngle > maxRevolutions*360 && maxRevolutions != 0){
                    cancel();
                }
            }
        }.runTaskTimer(Core.getInstance(),0, ticks);
    }
}
