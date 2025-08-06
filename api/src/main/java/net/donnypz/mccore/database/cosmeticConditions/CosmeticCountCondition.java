package net.donnypz.mccore.database.cosmeticConditions;

import net.donnypz.mccore.cosmetics.Cosmetic;
import net.donnypz.mccore.cosmetics.CosmeticRegistry;
import net.donnypz.mccore.database.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CosmeticCountCondition implements CosmeticCondition{

    private final CosmeticRegistry<?> registry;
    private final int minimum;
    private final String displayName;

    public CosmeticCountCondition(@NotNull CosmeticRegistry<? extends Cosmetic> registry, int minimum, @NotNull String displayName){
        this.registry = registry;
        this.minimum = minimum;
        this.displayName = displayName;
    }

    @Override
    public boolean meetsCondition(Document document, UUID uuid) {
        return PlayerData.get(uuid).getUnlockedCosmetics(registry) >= minimum;
    }

    @Override
    public Component buildLore(Cosmetic cosmetic) {
        return Component.text("Requires at least: ", NamedTextColor.GRAY)
                .append(Component.text(String.valueOf(minimum), NamedTextColor.YELLOW))
                .append(Component.space())
                .append(Component.text(displayName, NamedTextColor.GRAY))
                .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }
}
