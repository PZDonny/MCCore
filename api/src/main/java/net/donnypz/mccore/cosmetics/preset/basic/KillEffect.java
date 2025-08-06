package net.donnypz.mccore.cosmetics.preset.basic;

import net.donnypz.mccore.cosmetics.Cosmetic;
import net.donnypz.mccore.cosmetics.CosmeticRegistry;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class KillEffect extends Cosmetic {
    public KillEffect(@NotNull String cosmeticName, CosmeticRegistry<? extends KillEffect> registry) {
        super(cosmeticName, registry);
    }

    public abstract void playKillEffect(Player killer, LivingEntity victim);
}
