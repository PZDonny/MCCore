package net.donnypz.mccore.cosmetics.preset.parrotpet;

import net.donnypz.mccore.cosmetics.CosmeticRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Parrot;

public class ParrotPetRegistry extends CosmeticRegistry<ParrotPet> {

    public ParrotPetRegistry() {
        super(ParrotPet.class);
    }

    @Override
    protected void registerCosmetics() {
        for (Parrot.Variant variant : Parrot.Variant.values()){
            new ParrotPet(variant.name(), variant, this)
                    .setCosmeticDisplayName(Component.text(variant.name().charAt(0)+variant.name().toLowerCase().substring(1), NamedTextColor.YELLOW))
                    .setDisplayMaterial(Material.valueOf(variant.name()+"_DYE"));
        }
    }
}
