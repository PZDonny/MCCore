package net.donnypz.mccore.cosmetics.armortrims;

import net.donnypz.mccore.cosmetics.CosmeticRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.meta.trim.TrimMaterial;

public class ArmorTrimMaterialRegistry extends CosmeticRegistry {
    @Override
    protected void registerCosmetics() {
        for (TrimMaterialCosmetic material : TrimMaterialCosmetic.values()){
            new ArmorTrimMaterial(material.material, this)
                    .setCosmeticDisplayName(Component.text(material.displayName, NamedTextColor.YELLOW))
                    .setValue(material.name());
        }
    }

    public enum TrimMaterialCosmetic{
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
        TrimMaterialCosmetic(Material material, String displayName, TrimMaterial trimMaterial){
            this.material = material;
            this.displayName = displayName;
            this.trimMaterial = trimMaterial;
        }

        public TrimMaterial getTrimMaterial() {
            return trimMaterial;
        }
    }


}
