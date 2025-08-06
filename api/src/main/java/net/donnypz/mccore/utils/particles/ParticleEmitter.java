package net.donnypz.mccore.utils.particles;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;

public abstract class ParticleEmitter {
    int amount = 1;

    public void setParticleAmount(int amount){
        this.amount = Math.abs(amount);
    }

    protected abstract void spawnParticle(Location location);

    protected abstract void spawnParticle(Collection<Player> players, Location location);
}
