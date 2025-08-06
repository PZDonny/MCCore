package net.donnypz.mccore.cosmetics.preset.armortrims;

import org.bukkit.inventory.meta.trim.TrimPattern;

public interface TrimPatternCosmetic extends TrimCosmetic{

    TrimPattern getPattern();

    TrimPatternCosmetic[] getCosmetics();
}
