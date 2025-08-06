package net.donnypz.mccore.cosmetics.preset.shieldSkins.holiday;

import net.donnypz.mccore.cosmetics.preset.shieldSkins.ShieldPattern;
import net.donnypz.mccore.cosmetics.preset.shieldSkins.ShieldSkin;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.PatternType;

public class Christmas extends ShieldSkin {
    public Christmas() {
        super("christmas", null);

        ShieldPattern pattern = new ShieldPattern();
        pattern
            .addPattern(DyeColor.WHITE, PatternType.STRIPE_TOP)
            .addPattern(DyeColor.BLACK, PatternType.CREEPER)
            .addPattern(DyeColor.RED, PatternType.TRIANGLE_TOP)
            .addPattern(DyeColor.LIME, PatternType.BASE);
        setShieldPattern(pattern);
    }
}
