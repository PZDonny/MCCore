package net.donnypz.mccore.cosmetics.preset.armortrims;

import org.bukkit.inventory.meta.trim.TrimMaterial;

public interface TrimMaterialCosmetic extends TrimCosmetic{

    TrimMaterial getTrimMaterial();

    TrimMaterialCosmetic[] getCosmetics();
}
