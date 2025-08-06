package net.donnypz.mccore.version.v1_21_5;

import net.donnypz.mccore.cosmetics.preset.armortrims.TrimPatternCosmetic;
import org.bukkit.Material;
import org.bukkit.inventory.meta.trim.TrimPattern;

public enum TrimPatternCosmeticImpl implements TrimPatternCosmetic {
    SENTRY(Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, "Sentry", TrimPattern.SENTRY),
    VEX(Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE,"Vex", TrimPattern.VEX),
    WILD(Material.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, "Wild", TrimPattern.WILD),
    COAST(Material.COAST_ARMOR_TRIM_SMITHING_TEMPLATE,  "Coast", TrimPattern.COAST),
    DUNE(Material.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE,  "Dune", TrimPattern.DUNE),
    WAYFINDER(Material.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE,  "Wayfinder", TrimPattern.WAYFINDER),
    RAISER(Material.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE,  "Raiser", TrimPattern.RAISER),
    SHAPER(Material.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE,  "Shaper", TrimPattern.SHAPER),
    HOST(Material.HOST_ARMOR_TRIM_SMITHING_TEMPLATE,  "Host", TrimPattern.HOST),
    WARD(Material.WARD_ARMOR_TRIM_SMITHING_TEMPLATE,  "Ward", TrimPattern.WARD),
    SILENCE(Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE,  "Silence", TrimPattern.SILENCE),
    TIDE(Material.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE,  "Tide", TrimPattern.TIDE),
    SNOUT(Material.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE,  "Snout", TrimPattern.SNOUT),
    RIB(Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE,  "Rib", TrimPattern.RIB),
    EYE(Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE,  "Eye", TrimPattern.EYE),
    SPIRE(Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE,  "Spire", TrimPattern.SPIRE),
    FLOW(Material.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE,  "Flow", TrimPattern.FLOW),
    BOLT(Material.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE,  "Bolt", TrimPattern.BOLT);

    final Material material;
    final String displayName;
    final TrimPattern pattern;

    TrimPatternCosmeticImpl(Material material, String displayName, TrimPattern pattern){
        this.material = material;
        this.displayName = displayName;
        this.pattern = pattern;
    }

    @Override
    public String getCosmeticName() {
        return name().toLowerCase();
    }


    public Material getMaterial(){
        return material;
    }

    public String getDisplayName(){
        return displayName;
    }

    public TrimPattern getPattern() {
        return pattern;
    }

    @Override
    public TrimPatternCosmetic[] getCosmetics() {
        return TrimPatternCosmeticImpl.values();
    }
}
