package MCCore.cosmetics;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

public class MaterialCosmetic extends Cosmetic {
    Material material;
    BlockData blockData;

    public MaterialCosmetic(Material material) {
        super(material.name().toLowerCase());
        this.material = material;
        this.blockData = material.createBlockData();
        this.setDisplayMaterial(material);
    }

    public MaterialCosmetic(String cosmeticName, Material material) {
        super(cosmeticName);
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
