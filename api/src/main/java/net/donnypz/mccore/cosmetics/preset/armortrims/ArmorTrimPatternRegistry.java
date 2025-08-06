package net.donnypz.mccore.cosmetics.preset.armortrims;

import net.donnypz.mccore.cosmetics.CosmeticRegistry;
import net.donnypz.mccore.version.CoreAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ArmorTrimPatternRegistry extends CosmeticRegistry<ArmorTrimPattern> {

    public ArmorTrimPatternRegistry() {
        super(ArmorTrimPattern.class);
    }

    @Override
    protected void registerCosmetics() {
        for (TrimPatternCosmetic trim : CoreAPI.getTrimPatternCosmetic()){
            new ArmorTrimPattern(trim, this)
                    .setCosmeticDisplayName(Component.text(trim.getDisplayName(), NamedTextColor.YELLOW))
                    .setSelectValue(trim.getCosmeticName());
        }
    }
}
