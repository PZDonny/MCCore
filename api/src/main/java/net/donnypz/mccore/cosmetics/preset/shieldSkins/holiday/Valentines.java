package net.donnypz.mccore.cosmetics.preset.shieldSkins.holiday;

import net.donnypz.mccore.cosmetics.preset.shieldSkins.ShieldPattern;
import net.donnypz.mccore.cosmetics.preset.shieldSkins.ShieldSkin;
import net.donnypz.mccore.version.CoreAPI;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.PatternType;

public class Valentines extends ShieldSkin {
    public Valentines() {
        super("valentines", null);

        ShieldPattern pattern = new ShieldPattern();
        pattern.addPattern(DyeColor.BLACK, PatternType.BASE)
                .addPattern(DyeColor.GRAY, PatternType.GRADIENT)
                .addPattern(DyeColor.PINK, CoreAPI.getVersionHandler().getRhombusPattern())
                .addPattern(DyeColor.WHITE, PatternType.MOJANG)
                .addPattern(DyeColor.PINK, PatternType.GLOBE)
                .addPattern(DyeColor.GRAY, PatternType.TRIANGLE_TOP)
                .addPattern(DyeColor.GRAY, PatternType.STRIPE_TOP);
        setShieldPattern(pattern);
    }
}
