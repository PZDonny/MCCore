package MCCore.utils.ParticleShapes;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

public class DirectionalParticleRing {

    Location location;
    double radius;

    double angleToNext;
    double maxRevolutions = 1;

    double extra;

    int amount = 1;

    Random random = new Random();

    List<Particle> particles = new ArrayList<>();
    double offset = 0.1;

    public DirectionalParticleRing(Location location, double radius, double angleToNext, double extra){
        this.location = location.clone();
        this.radius = radius;
        this.angleToNext = angleToNext;
        this.extra = extra;
        this.particles.add(Particle.FLAME);
    }

    public DirectionalParticleRing setParticleAmount(int amount){
        this.amount = amount;
        return this;
    }

    public DirectionalParticleRing setOffset(double offset){
        if (offset <= 0) this.offset = 0.1;
        else this.offset = offset;
        return this;
    }

    public DirectionalParticleRing setLocation(Location loc){
        this.location = loc.clone();
        return this;
    }

    public DirectionalParticleRing setParticles(Particle... particles){
        if (particles == null || particles.length == 0) return this;
        this.particles.clear();
        this.particles = Arrays.asList(particles);
        return this;
    }

    public DirectionalParticleRing setMaxRevolutions(double maxRevolutions){
        this.maxRevolutions = Math.abs(maxRevolutions);
        return this;
    }


    public void spawn(){
        Location loc = location.clone();
        Location offset = location.clone();
        offset.setPitch(location.getPitch()+90F);

        Vector extendVector = loc.getDirection().multiply(this.offset);
        Vector offsetVector = offset.getDirection().normalize().multiply(radius);
        for (int i = 0; i < maxRevolutions*360; i+=angleToNext){
            Location particleLoc = loc.clone().add(offsetVector.rotateAroundAxis(extendVector, Math.toRadians(i)).normalize().multiply(radius));
            particleLoc.add(extendVector);
            Particle particle = particles.get(random.nextInt(particles.size()));
            particleLoc.getWorld().spawnParticle(particle, particleLoc, amount, 0,0,0, extra);
        }
    }

    public void spawn(Player player){
        if (!player.isOnline()) return;
        Location loc = location.clone();
        Location offset = location.clone();
        offset.setPitch(location.getPitch()+90F);

        Vector extendVector = loc.getDirection().multiply(this.offset);
        Vector offsetVector = offset.getDirection().normalize().multiply(radius);
        for (int i = 0; i < maxRevolutions*360; i+=angleToNext){
            Location particleLoc = loc.clone().add(offsetVector.rotateAroundAxis(extendVector, Math.toRadians(i)).normalize().multiply(radius));
            particleLoc.add(extendVector);
            Particle particle = particles.get(random.nextInt(particles.size()));
            player.spawnParticle(particle, particleLoc, amount, 0,0,0, extra);
        }
    }

    public void spawn(Collection<Player> players){
        Location loc = location.clone();
        Location offset = location.clone();
        offset.setPitch(location.getPitch()+90F);

        Vector extendVector = loc.getDirection().multiply(this.offset);
        Vector offsetVector = offset.getDirection().normalize().multiply(radius);
        for (int i = 0; i < maxRevolutions*360; i+=angleToNext){
            Location particleLoc = loc.clone().add(offsetVector.rotateAroundAxis(extendVector, Math.toRadians(i)).normalize().multiply(radius));
            particleLoc.add(extendVector);
            Particle particle = particles.get(random.nextInt(particles.size()));
            for (Player player : players){
                if (!player.isOnline()) continue;
                particleLoc.getWorld().spawnParticle(particle, particleLoc, amount, 0,0,0, extra);
            }

        }
    }


}
