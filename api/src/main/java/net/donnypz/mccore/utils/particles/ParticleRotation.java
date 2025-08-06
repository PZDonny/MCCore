package net.donnypz.mccore.utils.particles;

import net.donnypz.mccore.version.CoreAPI;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Random;
import java.util.function.Consumer;

public abstract class ParticleRotation extends ParticleEmitter{
    double radius;
    double maxRevolutions = 1;
    int amount = 1;
    int ticks = 1;
    double angle = 15;
    double startAngle = 0;
    boolean clockwise = true;
    boolean forceStopped = false;

    boolean respectEntityPitch = true;
    float customPitch = 0;

    boolean respectEntityYaw = true;
    float customYaw = 0;

    double verticalOffset;

    boolean persistOnDeath = false;

    static final Random random = new Random();

    protected ParticleRotation(@NotNull Entity entity, double radius){
        this.verticalOffset = entity.getHeight()/2;
        this.radius = radius;
    }


    public ParticleRotation setPersistentOnDeath(boolean persist){
        this.persistOnDeath = persist;
        return this;
    }


    public ParticleRotation setTicksBetweenRotation(int ticks){
        if (ticks <= 0) ticks = 1;
        this.ticks = ticks;
        return this;
    }

    public ParticleRotation respectEntityPitch(boolean respect){
        respectEntityPitch = respect;
        return this;
    }

    public ParticleRotation setCustomPitch(float customPitch){
        respectEntityPitch = false;
        this.customPitch = customPitch;
        return this;
    }

    public ParticleRotation respectEntityYaw(boolean respect){
        respectEntityYaw = respect;
        return this;
    }

    public ParticleRotation setCustomYaw(float customYaw){
        respectEntityYaw = false;
        this.customYaw = customYaw;
        return this;
    }

    public ParticleRotation setStartAngle(double startAngle){
        this.startAngle = startAngle;
        return this;
    }


    public ParticleRotation setAngleToNextParticle(double angle){
        this.angle = angle;
        return this;
    }

    public ParticleRotation setRotateClockwise(boolean clockwise){
        this.clockwise = clockwise;
        return this;
    }

    public ParticleRotation setVerticalOffset(double verticalOffset){
        this.verticalOffset = verticalOffset;
        return this;
    }

    public ParticleRotation setMaxRevolutions(double maxRevolutions){
        this.maxRevolutions = Math.abs(maxRevolutions);
        return this;
    }


    public double getRadius() {
        return radius;
    }

    public double getMaxRevolutions() {
        return maxRevolutions;
    }

    public int getAmount() {
        return amount;
    }

    public int getTicks() {
        return ticks;
    }

    public double getAngle() {
        return angle;
    }

    public boolean isClockwise() {
        return clockwise;
    }

    public boolean isForceStopped() {
        return forceStopped;
    }

    public boolean respectEntityPitch() {
        return respectEntityPitch;
    }

    public float getCustomPitch() {
        return customPitch;
    }

    public boolean respectEntityYaw() {
        return respectEntityYaw;
    }

    public float getCustomYaw() {
        return customYaw;
    }

    public double getVerticalOffset() {
        return verticalOffset;
    }

    public boolean isPersistOnDeath() {
        return persistOnDeath;
    }


    public void spawn(@NotNull Entity entity, @Nullable Consumer<Location> consumer){
        forceStopped = false;
        new BukkitRunnable(){
            double currentAngle = startAngle;
            public void run(){
                if (!persistOnDeath && entity.isDead()){
                    cancel();
                    return;
                }
                Location loc = entity.getLocation().clone();
                //Centers the Particle to middle of entity
                loc.setY(loc.getY()+verticalOffset);
                Location offset = loc.clone();
                //Use Custom Pitch or Respect Entity
                if (!respectEntityPitch){
                    loc.setPitch(customPitch);
                    offset.setPitch(customPitch+90F);
                }
                else{
                    offset.setPitch(loc.getPitch()+90F);
                }
                //Use Custom Yaw or Respect Entity
                if (!respectEntityYaw){
                    loc.setYaw(customYaw);
                    offset.setYaw(customYaw);
                }

                Vector extendVector = loc.getDirection().multiply(0.01); //Forward Spacing
                Vector offsetVector = offset.getDirection().normalize().multiply(radius);

                Location particleLoc;
                if (currentAngle < startAngle){
                    particleLoc = loc.add(offsetVector.rotateAroundAxis(extendVector, Math.toRadians(startAngle)).normalize().multiply(radius));
                    currentAngle = startAngle;
                }
                else{
                    if (clockwise){
                        particleLoc = loc.add(offsetVector.rotateAroundAxis(extendVector, Math.toRadians(currentAngle)).normalize().multiply(radius));
                    }
                    else{
                        particleLoc = loc.add(offsetVector.rotateAroundAxis(extendVector, Math.toRadians(currentAngle)).normalize().multiply(radius*-1));
                    }
                }

                spawnParticle(particleLoc);
                if (consumer != null){
                    consumer.accept(particleLoc);
                }

                //loc.add(extendVector);

                currentAngle+=angle;

                if ((currentAngle > (360*maxRevolutions) && maxRevolutions != 0)|| forceStopped){
                    cancel();
                }
            }

        }.runTaskTimer(CoreAPI.getPlugin(),0, ticks);
    }

    protected void spawnParticle(Collection<Player> players, Location location){}

    public void stop(){
        forceStopped = true;
    }
}
