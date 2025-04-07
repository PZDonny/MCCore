package net.donnypz.mccore.cosmetics;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public abstract class KillEffect extends Cosmetic{
    public KillEffect(String cosmeticName, CosmeticRegistry registry) {
        super(cosmeticName, registry);
    }

    public abstract void playKillEffect(Player killer, LivingEntity victim);
}
