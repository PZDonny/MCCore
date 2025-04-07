package net.donnypz.mccore.utils.particles;

import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

public class ParticleUtils {


    public static void drawParticleLine(Location start, double distance, Particle particle, int particleAmount, double particleSpacing, double extra) {
        drawParticleLine(start, distance, particle, particleAmount, particleSpacing, 0, 0, 0, extra);
    }
    
    public static void drawParticleLine(Location start, double distance, Particle particle, int particleAmount, double particleSpacing, double xOffset, double yOffset, double zOffset, double extra) {
        Vector vector = start.getDirection().multiply(distance);
        Location to = start.clone();
        to.add(vector);
        drawParticleLine(start, to, particle, particleAmount, particleSpacing, xOffset, yOffset, zOffset, extra);
    }
    
    public static void drawParticleLine(Location from, Location to, Particle particle, int particleAmount, double particleSpacing, double extra) {
        drawParticleLine(from, to, particle, particleAmount, particleSpacing, 0, 0, 0, extra);
    }
    
    public static void drawParticleLine(Location from, Location to, Particle particle, int particleAmount, double particleSpacing, double xOffset, double yOffset, double zOffset, double extra) {
        double distance = from.distance(to);
        for(double i = 0; i<distance; i+= particleSpacing) {
            Vector vector  = from.clone().toVector().subtract(to.toVector()).normalize().multiply(-i);
            Location loc = from.clone().add(vector);
            loc.getWorld().spawnParticle(particle, loc, particleAmount, xOffset, yOffset, zOffset, extra);
        }
    }



    //-------|BlockCrack Particles|--------
    
    public static void drawBlockCrackLine(Location start, double distance, BlockData blockData, int particleAmount, double particleSpacing, double extra) {
        drawBlockCrackLine(start, distance, blockData, particleAmount, particleSpacing, 0, 0, 0, extra);
    }

    public static void drawBlockCrackLine(Location start, double distance, BlockData blockData, int particleAmount, double particleSpacing, double xOffset, double yOffset, double zOffset, double extra) {
        Vector vector = start.getDirection().multiply(distance);
        Location to = start.clone();
        to.add(vector);
        drawBlockCrackLine(start, to, blockData, particleAmount, particleSpacing, xOffset, yOffset, zOffset, extra);
    }

    public static void drawBlockCrackLine(Location from, Location to, BlockData blockData, int particleAmount, double particleSpacing, double extra) {
        drawBlockCrackLine(from, to, blockData, particleAmount, particleSpacing, 0, 0, 0, extra);
    }

    public static void drawBlockCrackLine(Location from, Location to, BlockData blockData, int particleAmount, double particleSpacing, double xOffset, double yOffset, double zOffset, double extra) {
        double distance = from.distance(to);
        for(double i = 0; i<distance; i+= particleSpacing) {
            Vector vector  = from.clone().toVector().subtract(to.toVector()).normalize().multiply(-i);
            Location loc = from.clone().add(vector);
            loc.getWorld().spawnParticle(Particle.BLOCK, loc, particleAmount, xOffset, yOffset, zOffset, extra, blockData);
        }
    }

    //-------|ItemCrack Particles|--------

    public static void drawItemCrackLine(Location start, double distance, Material material, int particleAmount, double particleSpacing, double extra) {
        drawItemCrackLine(start, distance, material, particleAmount, particleSpacing, 0, 0, 0, extra);
    }

    public static void drawItemCrackLine(Location start, double distance, Material material, int particleAmount, double particleSpacing, double xOffset, double yOffset, double zOffset, double extra) {
        Vector vector = start.getDirection().multiply(distance);
        Location to = start.clone();
        to.add(vector);
        drawItemCrackLine(start, to, material, particleAmount, particleSpacing, xOffset, yOffset, zOffset, extra);
    }

    public static void drawItemCrackLine(Location from, Location to, Material material, int particleAmount, double particleSpacing, double extra) {
        drawItemCrackLine(from, to, material, particleAmount, particleSpacing, 0, 0, 0, extra);
    }

    public static void drawItemCrackLine(Location from, Location to, Material material, int particleAmount, double particleSpacing, double xOffset, double yOffset, double zOffset, double extra) {
        double distance = from.distance(to);
        for(double i = 0; i<distance; i+= particleSpacing) {
            Vector vector  = from.clone().toVector().subtract(to.toVector()).normalize().multiply(-i);
            Location loc = from.clone().add(vector);
            loc.getWorld().spawnParticle(Particle.BLOCK, loc, particleAmount, xOffset, yOffset, zOffset, extra, material);
        }
    }


    //-------|Vibration Particle|--------

    public static void sendVibrationToEntity(Location from, Entity entity, int particleAmount, int arrivalTime){
        sendVibrationToEntity(from, entity, particleAmount, arrivalTime, 0,0,0);
    }

    public static void sendVibrationToEntity(Location from, Entity entity, int particleAmount, int arrivalTime, double xOffset, double yOffset, double zOffset) {
        from.getWorld().spawnParticle(Particle.VIBRATION, from, particleAmount, xOffset, yOffset, zOffset, new Vibration(new Vibration.Destination.EntityDestination(entity), arrivalTime));
    }

    public static void sendVibrationToLocation(Location from, Location to, int particleAmount, int arrivalTime){
        sendVibrationToLocation(from, to, particleAmount, arrivalTime, 0,0,0);
    }

    public static void sendVibrationToLocation(Location from, Location to, int particleAmount, int arrivalTime, double xOffset, double yOffset, double zOffset) {
        from.getWorld().spawnParticle(Particle.VIBRATION, from, particleAmount, xOffset, yOffset, zOffset, new Vibration(new Vibration.Destination.BlockDestination(to), arrivalTime));
    }
    

    //-------|Redstone Particles|--------

    public static void drawRedstoneLine(Location start, double distance, Color color, int particleAmount, double particleSpacing, float size) {
        drawRedstoneLine(start, distance, color, particleAmount, particleSpacing, size, 0, 0,0);
    }
    
    public static void drawRedstoneLine(Location start, double distance, Color color, int particleAmount, double particleSpacing, float size, double xOffset, double yOffset, double zOffset) {
        Vector vector = start.getDirection().multiply(distance);
        Location to = start.clone();
        to.add(vector);
        drawRedstoneLine(start, to, color, particleAmount, particleSpacing, size, xOffset, yOffset, zOffset);
    }
    
    public static void drawRedstoneLine(Location from, Location to, Color color, int particleAmount, double particleSpacing, float size) {
        drawRedstoneLine(from, to, color,particleAmount, particleSpacing, size, 0, 0, 0);
    }

    
    public static void drawRedstoneLine(Location from, Location to, Color color, int particleAmount, double particleSpacing, float size, double xOffset, double yOffset, double zOffset) {
        double distance = from.distance(to);
        for(double i = 0; i<distance; i+= particleSpacing) {
            Vector vector  = from.clone().toVector().subtract(to.toVector()).normalize().multiply(-i);
            Location loc = from.clone().add(vector);
            loc.getWorld().spawnParticle(Particle.DUST, loc, particleAmount, xOffset, yOffset, zOffset, new Particle.DustOptions(color, size));
        }
    }
}
