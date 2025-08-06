package net.donnypz.mccore.cosmetics.preset.armortrims;

import net.donnypz.mccore.cosmetics.Cosmetic;
import org.bukkit.Material;
import org.bukkit.inventory.meta.trim.TrimPattern;

public class ArmorTrimPattern extends Cosmetic {
    TrimPattern trimPattern;
    ArmorTrimPattern(TrimPatternCosmetic cosmetic, ArmorTrimPatternRegistry registry) {
        super(cosmetic.getCosmeticName(), registry);
        this.setDisplayMaterial(cosmetic.getMaterial());
        this.trimPattern = cosmetic.getPattern();
    }

    public TrimPattern getTrimPattern() {
        return trimPattern;
    }
}
