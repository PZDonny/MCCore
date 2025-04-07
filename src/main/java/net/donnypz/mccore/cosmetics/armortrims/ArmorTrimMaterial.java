package net.donnypz.mccore.cosmetics.armortrims;

import net.donnypz.mccore.cosmetics.Cosmetic;
import org.bukkit.Material;

public class ArmorTrimMaterial extends Cosmetic {
    ArmorTrimMaterial(Material material, ArmorTrimMaterialRegistry registry) {
        super(material.name().toLowerCase(), registry);
        this.setDisplayMaterial(material);
    }
}
