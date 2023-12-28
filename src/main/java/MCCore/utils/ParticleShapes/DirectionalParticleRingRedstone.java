package MCCore.utils.ParticleShapes;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

public class DirectionalParticleRingRedstone {

    Location location;
    double radius;

    double angleToNext;

    double maxRevolutions = 1;


    int amount = 1;

    Random random = new Random();

    List<Color> colors = new ArrayList<>();
    double offset = 0.1;

    float particleSize = 1;

    public DirectionalParticleRingRedstone(Location location, double radius, double angleToNext){
        this.location = location.clone();
        this.radius = radius;
        if (angleToNext <= 0) angleToNext = 1;
        this.angleToNext = angleToNext;
        this.colors.add(Color.RED);
    }

    public DirectionalParticleRingRedstone setParticleAmount(int amount){
        this.amount = amount;
        return this;
    }

    public DirectionalParticleRingRedstone setOffset(double offset){
        if (offset <= 0) this.offset = 0.1;
        else this.offset = offset;
        return this;
    }

    public DirectionalParticleRingRedstone setLocation(Location loc){
        this.location = loc.clone();
        return this;
    }

    public DirectionalParticleRingRedstone setColors(Color... colors){
        if (colors == null || colors.length == 0) return this;
        this.colors.clear();
        this.colors = Arrays.asList(colors);
        return this;
    }

    public DirectionalParticleRingRedstone setParticleSize(float size){
        this.particleSize = size;
        return this;
    }

    public DirectionalParticleRingRedstone setMaxRevolutions(double maxRevolutions){
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
            Color color = colors.get(random.nextInt(colors.size()));
            particleLoc.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, amount, 0,0,0, new Particle.DustOptions(color, particleSize));
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
            Color color = colors.get(random.nextInt(colors.size()));
            player.spawnParticle(Particle.REDSTONE, particleLoc, amount, 0,0,0, new Particle.DustOptions(color, particleSize));
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
            Color color = colors.get(random.nextInt(colors.size()));
            for (Player player : players){
                if (!player.isOnline()) continue;
                player.spawnParticle(Particle.REDSTONE, particleLoc, amount, 0,0,0, new Particle.DustOptions(color, particleSize));
            }

        }
    }
}
