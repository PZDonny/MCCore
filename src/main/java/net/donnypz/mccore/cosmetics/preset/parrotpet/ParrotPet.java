package net.donnypz.mccore.cosmetics.preset.parrotpet;

import net.donnypz.mccore.cosmetics.Cosmetic;
import org.bukkit.entity.Parrot;
import org.jetbrains.annotations.NotNull;

public class ParrotPet extends Cosmetic {
    Parrot.Variant parrotVariant;
    ParrotPet(@NotNull String cosmeticName, Parrot.Variant variant, ParrotPetRegistry registry) {
        super(cosmeticName, registry);
        this.parrotVariant = variant;
    }

    public Parrot.Variant getParrotVariant() {
        return parrotVariant;
    }
}
