package net.donnypz.mccore.utils.particles;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public abstract class ParticleRing extends ParticleEmitter{
    double radius;
    double angleToNext;
    double startAngle = 0;
    double maxRevolutions = 1;
    double offset = 0.01;
    static final Random random = new Random();

    ParticleRing(double radius, double angleToNext){
        this.radius = radius;
        this.angleToNext = angleToNext <= 0 ? 1 : angleToNext;
    }

    public ParticleRing setRadius(double radius) {
        this.radius = radius;
        return this;
    }

    public ParticleRing setStartAngle(double startAngle){
        this.startAngle = startAngle;
        return this;
    }

    public ParticleRing setAngleToNext(double angleToNext) {
        this.angleToNext = angleToNext;
        return this;
    }

    public ParticleRing setMaxRevolutions(double maxRevolutions) {
        this.maxRevolutions = Math.abs(maxRevolutions);
        return this;
    }


    public ParticleRing setOffset(double offset){
        this.offset = offset <= 0 ? 0.01 : offset;
        return this;
    }

    public double getRadius() {
        return radius;
    }

    public double getAngleToNext() {
        return angleToNext;
    }

    public double getMaxRevolutions() {
        return maxRevolutions;
    }

    public double getOffset() {
        return offset;
    }

    public void spawn(@NotNull Location location, @Nullable Consumer<Location> runningAction){
        Location loc = location.clone();
        Location offset = location.clone();
        offset.setPitch(location.getPitch()+90F);

        Vector extendVector = loc.getDirection().multiply(this.offset);
        Vector offsetVector = offset.getDirection().normalize().multiply(radius);
        for (double i = startAngle; i < maxRevolutions*360; i+=angleToNext){
            Location particleLoc = loc.clone().add(offsetVector.rotateAroundAxis(extendVector, Math.toRadians(i)).normalize().multiply(radius));
            particleLoc.add(extendVector);
            spawnParticle(particleLoc);

            if (runningAction != null){
                runningAction.accept(particleLoc);
            }
        }
    }

    public void spawn(@NotNull Location location, @NotNull Player player, @Nullable Consumer<Location> runningAction){
        if (!player.isOnline()){
            return;
        }
        spawn(location, List.of(player), runningAction);
    }

    public void spawn(@NotNull Location location, @NotNull Collection<Player> players, @Nullable Consumer<Location> runningAction){
        Location loc = location.clone();
        Location offset = location.clone();
        offset.setPitch(location.getPitch()+90F);

        Vector extendVector = loc.getDirection().multiply(this.offset);
        Vector offsetVector = offset.getDirection().normalize().multiply(radius);
        for (double i = startAngle; i < maxRevolutions*360; i+=angleToNext){
            Location particleLoc = loc.clone().add(offsetVector.rotateAroundAxis(extendVector, Math.toRadians(i)).normalize().multiply(radius));
            particleLoc.add(extendVector);
            spawnParticle(players, particleLoc);
            if (runningAction != null){
                runningAction.accept(particleLoc);
            }
        }
    }
}
