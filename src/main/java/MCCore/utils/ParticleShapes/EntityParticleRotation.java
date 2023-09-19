package MCCore.utils.ParticleShapes;

import MCCore.Core;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class EntityParticleRotation {

    double radius;

    double forwardSpacing;

    double maxRevolutions = 1;

    double extra;

    int amount = 1;

    int ticks = 1;

    double angle = 15;
    boolean clockwise = true;

    Random random = new Random();
    Entity entity;

    List<Particle> particles= new ArrayList<>();

    boolean forceStopped = false;

    boolean respectEntityPitch = true;

    float customPitch = 0;

    boolean respectEntityYaw = true;

    float customYaw = 0;

    double verticalOffset;



    public EntityParticleRotation(Entity entity, double radius, double forwardSpacing, double extra){
        this.entity = entity;
        this.radius = radius;
        this.forwardSpacing = forwardSpacing;
        this.extra = extra;
        this.verticalOffset = entity.getHeight()/2;
        this.particles.add(Particle.FLAME);
    }

    public EntityParticleRotation setParticleAmount(int amount){
        this.amount = amount;
        return this;
    }

    public EntityParticleRotation setTicksBetweenRotation(int ticks){
        if (ticks <= 0) ticks = 1;
        this.ticks = ticks;
        return this;
    }

    public EntityParticleRotation setParticles(Particle... particles){
        if (particles == null || particles.length == 0) return this;
        this.particles.clear();
        this.particles = Arrays.asList(particles);
        return this;
    }

    public EntityParticleRotation respectEntityPitch(boolean respect){
        respectEntityPitch = respect;
        return this;
    }

    public EntityParticleRotation setCustomPitch(float customPitch){
        respectEntityPitch = false;
        this.customPitch = customPitch;
        return this;
    }

    public EntityParticleRotation respectEntityYaw(boolean respect){
        respectEntityYaw = respect;
        return this;
    }

    public EntityParticleRotation setCustomYaw(float customYaw){
        respectEntityYaw = false;
        this.customYaw = customYaw;
        return this;
    }

    public EntityParticleRotation setAngleToNextParticle(double angle){
        this.angle = angle;
        return this;
    }

    public EntityParticleRotation setRotateClockwise(boolean clockwise){
        this.clockwise = clockwise;
        return this;
    }

    public EntityParticleRotation setVerticalOffset(double verticalOffset){
        this.verticalOffset = verticalOffset;
        return this;
    }

    public EntityParticleRotation setMaxRevolutions(double maxRevolutions){
        this.maxRevolutions = Math.abs(maxRevolutions);
        return this;
    }


    public void spawn(){
        forceStopped = false;
        new BukkitRunnable(){

            double currentAngle = 0;
            public void run(){
                Location loc = entity.getLocation().clone();
            //Centers the Particle to middle of entity
                loc.setY(loc.getY()+verticalOffset);
                Location offset = loc.clone();
            //Use Custom Pitch or Respect Entity
                if (!respectEntityPitch){
                    loc.setPitch(customPitch);
                    offset.setPitch(customPitch+90F);
                }
                else offset.setPitch(loc.getPitch()+90F);
            //Use Custom Yaw or Respect Entity
                if (!respectEntityYaw){
                    loc.setYaw(customYaw);
                    offset.setYaw(customYaw);
                }

                Vector extendVector = loc.getDirection().multiply(forwardSpacing);
                Vector offsetVector = offset.getDirection().normalize().multiply(radius);

                Location particleLoc;
                if (clockwise) particleLoc = loc.add(offsetVector.rotateAroundAxis(extendVector, Math.toRadians(currentAngle)).normalize().multiply(radius));
                else particleLoc = loc.add(offsetVector.rotateAroundAxis(extendVector, Math.toRadians(currentAngle*-1)).normalize().multiply(radius));

                Particle particle = particles.get(random.nextInt(particles.size()));
                particleLoc.getWorld().spawnParticle(particle, particleLoc, amount, 0,0,0, extra);
                loc.add(extendVector);
                if (clockwise) currentAngle+=angle;
                else currentAngle-=angle;
                if ((currentAngle > (360*maxRevolutions) && maxRevolutions != 0)|| forceStopped){
                    cancel();
                }
            }

        }.runTaskTimer(Core.getInstance(),0, ticks);
    }

    public void stop(){
        forceStopped = true;
    }
}
