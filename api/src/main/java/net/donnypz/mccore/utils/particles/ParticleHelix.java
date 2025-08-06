package net.donnypz.mccore.utils.particles;

import net.donnypz.mccore.version.CoreAPI;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public abstract class ParticleHelix extends ParticleEmitter{
    double radius;
    double maxRevolutions;
    int amount = 1;
    int ticks = 1;
    double angle = 15;
    double startAngle = 0;
    boolean clockwise = true;
    double forwardOffset;
    static final Random random = new Random();
    boolean forceStopped = false;

    ParticleHelix(double radius, double maxRevolutions, double forwardOffset){
        this.radius = radius;
        this.maxRevolutions = maxRevolutions;
        this.forwardOffset = forwardOffset;
    }

    public ParticleHelix setTicksBetweenRotation(int ticks){
        if (ticks <= 0){
            ticks = 1;
        }
        this.ticks = ticks;
        return this;
    }

    public ParticleHelix setStartAngle(double startAngle){
        this.startAngle = startAngle;
        return this;
    }

    public ParticleHelix setAngleToNextParticle(double angle){
        this.angle = angle;
        return this;
    }

    public ParticleHelix setRotateClockwise(boolean clockwise){
        this.clockwise = clockwise;
        return this;
    }

    public void spawn(@NotNull Location location, @Nullable Consumer<Location> runningAction){
        Location loc = location.clone();

        Vector relativeVector = loc.getDirection().multiply(0.01);
        Vector forwardOffsetVector = forwardOffset == 0 ? null : loc.getDirection().multiply(forwardOffset);

        Location edge = location.clone();
        edge.setPitch(edge.getPitch()+90);
        Vector radiusVector = edge.getDirection().normalize().multiply(radius);

        new BukkitRunnable(){
            double currentAngle = 0;
            public void run(){
                if (forceStopped){
                    cancel();
                    return;
                }

                Location particleLoc;
                if (currentAngle < startAngle){
                    particleLoc = loc.clone().add(radiusVector.rotateAroundAxis(relativeVector, Math.toRadians(startAngle)).normalize().multiply(radius));
                    currentAngle = startAngle;
                }
                else{
                    if (clockwise){
                        particleLoc = loc.clone().add(radiusVector.rotateAroundAxis(relativeVector, Math.toRadians(angle)).normalize().multiply(radius));
                    }
                    else{
                        particleLoc = loc.clone().add(radiusVector.rotateAroundAxis(relativeVector, Math.toRadians(angle*-1)).normalize().multiply(radius));
                    }
                }


                spawnParticle(particleLoc);
                if (runningAction != null){
                    runningAction.accept(particleLoc);
                }

                //Add Forward Offset
                if (forwardOffsetVector != null){
                    loc.add(forwardOffsetVector);
                }


                currentAngle+=angle;
                if (maxRevolutions > 0 && currentAngle > maxRevolutions*360){
                    cancel();
                }
            }
        }.runTaskTimer(CoreAPI.getPlugin(),0, ticks);

    }
    
    public void spawn(@NotNull Location location, @NotNull Player player, @Nullable Consumer<Location> runningAction){
        if (!player.isOnline()){
            return;
        }
        spawn(location, List.of(player), runningAction);
    }

    public void spawn(@NotNull Location location, @NotNull Collection<Player> players, @Nullable Consumer<Location> runningAction){
        Location loc = location.clone();

        Vector relativeVector = loc.getDirection().multiply(0.01);
        Vector forwardOffsetVector = forwardOffset == 0 ? null : loc.getDirection().multiply(forwardOffset);

        Location edge = location.clone();
        edge.setPitch(edge.getPitch()+90);
        Vector radiusVector = edge.getDirection().normalize().multiply(radius);


        new BukkitRunnable(){
            double currentAngle = 0;
            public void run(){
                if (forceStopped){
                    cancel();
                    return;
                }

                Location particleLoc;
                if (currentAngle < startAngle){
                    particleLoc = loc.clone().add(radiusVector.rotateAroundAxis(relativeVector, Math.toRadians(startAngle)).normalize().multiply(radius));
                    currentAngle = startAngle;
                }
                else{
                    if (clockwise){
                        particleLoc = loc.clone().add(radiusVector.rotateAroundAxis(relativeVector, Math.toRadians(angle)).normalize().multiply(radius));
                    }
                    else{
                        particleLoc = loc.clone().add(radiusVector.rotateAroundAxis(relativeVector, Math.toRadians(angle*-1)).normalize().multiply(radius));
                    }
                }

                spawnParticle(players, particleLoc);
                if (runningAction != null){
                    runningAction.accept(particleLoc);
                }

                //Add Forward Offset
                if (forwardOffsetVector != null){
                    loc.add(forwardOffsetVector);
                }

                currentAngle+=angle;
                if (maxRevolutions > 0 && currentAngle > maxRevolutions*360){
                    cancel();
                }
            }
        }.runTaskTimer(CoreAPI.getPlugin(),0, ticks);
    }

    public void stop(){
        forceStopped = true;
    }
}
