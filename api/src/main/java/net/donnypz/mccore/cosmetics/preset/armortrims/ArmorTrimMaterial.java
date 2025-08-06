package net.donnypz.mccore.cosmetics.preset.armortrims;

import net.donnypz.mccore.cosmetics.Cosmetic;
import org.bukkit.Material;
import org.bukkit.inventory.meta.trim.TrimMaterial;

public class ArmorTrimMaterial extends Cosmetic {
    TrimMaterial trimMaterial;

    ArmorTrimMaterial(TrimMaterialCosmetic cosmetic, ArmorTrimMaterialRegistry registry){
        super(cosmetic.getCosmeticName(), registry);
        this.setDisplayMaterial(cosmetic.getMaterial());
        this.trimMaterial = cosmetic.getTrimMaterial();
    }

    public TrimMaterial getTrimMaterial(){
        return trimMaterial;
    }
}
