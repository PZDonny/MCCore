package net.donnypz.mccore.cosmetics.preset.armortrims;

import net.donnypz.mccore.cosmetics.Cosmetic;
import org.bukkit.Material;

public class ArmorTrimPattern extends Cosmetic {
    ArmorTrimPattern(Material material, ArmorTrimPatternRegistry registry) {
        super(material.name().toLowerCase(), registry);
        this.setDisplayMaterial(material);
    }
}
