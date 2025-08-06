package net.donnypz.mccore.cosmetics.preset.basic;

import net.donnypz.mccore.cosmetics.Cosmetic;
import net.donnypz.mccore.cosmetics.CosmeticRegistry;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

public class MaterialCosmetic extends Cosmetic {
    Material material;
    BlockData blockData;

    public MaterialCosmetic(Material material, CosmeticRegistry<? extends MaterialCosmetic> registry) {
        super(material.name().toLowerCase(), registry);
        this.material = material;
        this.blockData = material.createBlockData();
        this.setDisplayMaterial(material);
    }

    public MaterialCosmetic(@NotNull String cosmeticName, Material material, CosmeticRegistry registry) {
        super(cosmeticName, registry);
        this.material = material;
        this.blockData = material.createBlockData();
        this.setDisplayMaterial(material);
    }

    public Material getMaterial() {
        return material;
    }

    public BlockData getBlockData(){
        return blockData;
    }
}
