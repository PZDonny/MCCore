package MCCore.utils.ParticleShapes;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PitchParticleRing {

    Location location;
    double ringSize;

    double spacing;

    int amount = 1;

    Random random = new Random();

    List<Particle> particleList = new ArrayList<>();

    public PitchParticleRing(Location location, double ringSize, double spacing){
        this.location = location;
        this.ringSize = ringSize;
        this.spacing = spacing;
        this.particleList.add(Particle.FLAME);
    }

    public void setAmountPerRotation(int amount){
        this.amount = amount;
    }


    public void setParticle(Particle particle){
        this.particleList.clear();
        this.particleList.add(particle);
    }

    public void setParticles(List<Particle> particleList){
        this.particleList = particleList;
    }


    public void spawn(){
        float pc = location.getPitch();
        location.setPitch(pc);
        Vector tVec = location.getDirection();


        tVec.normalize();
        Vector v = tVec.clone();
        v.setX(ringSize);
        v.setZ(ringSize);
        v.setY(ringSize);

        for (int i = 0; i <360; i+=spacing){
            Location particleLoc = location.clone();
            particleLoc.setPitch(pc);
            Vector addVec = v.rotateAroundAxis(tVec, i);
            particleLoc.add(addVec);

            Particle particle = particleList.get(random.nextInt(particleList.size()));
            particleLoc.getWorld().spawnParticle(particle, particleLoc, amount, 0,0,0,0);
        }
    }


}
