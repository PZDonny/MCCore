package MCCore.cosmetics.shieldSkins.holidaySkins;

import MCCore.cosmetics.shieldSkins.ShieldPattern;
import MCCore.cosmetics.shieldSkins.ShieldSkin;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.PatternType;

public class Valentines extends ShieldSkin {
    public Valentines() {
        super("valentines");

        ShieldPattern pattern = new ShieldPattern();
        pattern.addPattern(DyeColor.BLACK, PatternType.BASE)
                .addPattern(DyeColor.GRAY, PatternType.GRADIENT)
                .addPattern(DyeColor.PINK, PatternType.RHOMBUS_MIDDLE)
                .addPattern(DyeColor.WHITE, PatternType.MOJANG)
                .addPattern(DyeColor.PINK, PatternType.GLOBE)
                .addPattern(DyeColor.GRAY, PatternType.TRIANGLE_TOP)
                .addPattern(DyeColor.GRAY, PatternType.STRIPE_TOP);
        setShieldPattern(pattern);
    }
}
