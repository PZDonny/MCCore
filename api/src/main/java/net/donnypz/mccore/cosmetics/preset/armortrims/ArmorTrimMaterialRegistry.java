package net.donnypz.mccore.cosmetics.preset.armortrims;

import net.donnypz.mccore.cosmetics.CosmeticRegistry;
import net.donnypz.mccore.version.CoreAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ArmorTrimMaterialRegistry extends CosmeticRegistry<ArmorTrimMaterial> {

    public ArmorTrimMaterialRegistry() {
        super(ArmorTrimMaterial.class);
    }

    @Override
    protected void registerCosmetics() {
        for (TrimMaterialCosmetic material : CoreAPI.getTrimMaterialCosmetics()){
            new ArmorTrimMaterial(material, this)
                    .setCosmeticDisplayName(Component.text(material.getDisplayName(), NamedTextColor.YELLOW))
                    .setSelectValue(material.getCosmeticName());
        }
    }
}
