package MCCore.utils.ParticleShapes;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DirectionalParticleRingRedstone {

    Location location;
    double ringSize;

    double spacing;

    int amount = 1;

    List<Color> colors = new ArrayList<>();

    float particleSize;


    Random random = new Random();

    public DirectionalParticleRingRedstone(Location location, double ringSize, double spacing, Color color, float particleSize){
        this.location = location.clone();
        this.ringSize = ringSize;
        this.spacing = spacing;
        this.colors.add(color);
        this.particleSize = particleSize;
    }

    public DirectionalParticleRingRedstone(Location location, double ringSize, double spacing, List<Color> colors, float particleSize){
        this.location = location.clone();
        this.ringSize = ringSize;
        this.spacing = spacing;
        this.colors = colors;
        this.particleSize = particleSize;
    }

    public void setAmountPerRotation(int amount){
        this.amount = amount;
    }

    public void setLocation(Location loc){
        this.location = loc.clone();
    }

    public void spawn(){
        Location loc = location.clone();
        float pc = loc.getPitch();
        loc.setPitch(pc);
        Vector tVec = loc.getDirection();


        tVec.normalize();
        Vector v = tVec.clone();
        v.setX(ringSize);
        v.setZ(ringSize);
        v.setY(ringSize);

        for (int i = 0; i <360; i+=spacing){
            Location particleLoc = loc.clone();
            particleLoc.setPitch(pc);
            Vector addVec = v.rotateAroundAxis(tVec, i);
            particleLoc.add(addVec);

            Color color = colors.get(random.nextInt(colors.size()));
            particleLoc.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, amount, 0 ,0,0, new Particle.DustOptions(color, particleSize));
        }
    }
}
