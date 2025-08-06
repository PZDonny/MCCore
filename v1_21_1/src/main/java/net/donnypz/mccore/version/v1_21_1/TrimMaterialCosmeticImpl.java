package net.donnypz.mccore.version.v1_21_1;

import net.donnypz.mccore.cosmetics.preset.armortrims.TrimMaterialCosmetic;
import org.bukkit.Material;
import org.bukkit.inventory.meta.trim.TrimMaterial;

public enum TrimMaterialCosmeticImpl implements TrimMaterialCosmetic {

    QUARTZ(Material.QUARTZ,  "Quartz", TrimMaterial.QUARTZ),
    IRON(Material.IRON_INGOT,  "Iron", TrimMaterial.IRON),
    NETHERITE(Material.NETHERITE_INGOT,  "Netherite", TrimMaterial.NETHERITE),
    REDSTONE(Material.REDSTONE,"Redstone", TrimMaterial.REDSTONE),
    COPPER(Material.COPPER_INGOT,  "Copper", TrimMaterial.COPPER),
    GOLD(Material.GOLD_INGOT,  "Gold", TrimMaterial.GOLD),
    EMERALD(Material.EMERALD, "Emerald", TrimMaterial.EMERALD),
    DIAMOND(Material.DIAMOND,  "Diamond", TrimMaterial.DIAMOND),
    LAPIS(Material.LAPIS_LAZULI, "Lapis Lazuli", TrimMaterial.LAPIS),
    AMETHYST(Material.AMETHYST_SHARD,  "Amethyst", TrimMaterial.AMETHYST);

    final Material material;
    final String displayName;
    final TrimMaterial trimMaterial;

    TrimMaterialCosmeticImpl(Material material, String displayName, TrimMaterial trimMaterial){
        this.material = material;
        this.displayName = displayName;
        this.trimMaterial = trimMaterial;
    }

    @Override
    public String getCosmeticName() {
        return name().toLowerCase();
    }

    @Override
    public Material getMaterial(){
        return material;
    }

    @Override
    public String getDisplayName(){
        return displayName;
    }

    @Override
    public TrimMaterial getTrimMaterial() {
        return trimMaterial;
    }

    @Override
    public TrimMaterialCosmetic[] getCosmetics() {
        return TrimMaterialCosmeticImpl.values();
    }


}
