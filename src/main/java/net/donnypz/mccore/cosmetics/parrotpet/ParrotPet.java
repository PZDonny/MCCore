package net.donnypz.mccore.cosmetics.parrotpet;

import net.donnypz.mccore.cosmetics.Cosmetic;
import org.bukkit.entity.Parrot;

public class ParrotPet extends Cosmetic {
    Parrot.Variant parrotVariant;
    ParrotPet(String cosmeticName, Parrot.Variant variant, ParrotPetRegistry registry) {
        super(cosmeticName, registry);
        this.parrotVariant = variant;
    }

    public Parrot.Variant getParrotVariant() {
        return parrotVariant;
    }
}
