package net.donnypz.mccore.cosmetics.preset.basic;

import net.donnypz.mccore.cosmetics.Cosmetic;
import net.donnypz.mccore.cosmetics.CosmeticRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class KillMessage extends Cosmetic {

    private Component messagePrefix = Component.empty();
    private Component messageSuffix = Component.empty();

    public KillMessage(@NotNull String cosmeticName, CosmeticRegistry registry){
        super(cosmeticName, registry);
    }

    public Component getFullMessage(Player victim, Player killer){
        return getFullMessage(victim.displayName(), killer.displayName());
    }

    public Component getFullMessage(Component victimName, Component killerName){
        return Component.text("☠ ", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                .append(victimName)
                .append(messagePrefix)
                .appendSpace()
                .append(killerName)
                .append(messageSuffix);
    }

    public static Component getDefaultMessage(Player victim, Player killer){
        return getDefaultMessage(victim.displayName(), killer.displayName());
    }

    public static Component getDefaultMessage(Component victimName, Component killerName){
        return Component.text("☠ ", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                .append(victimName)
                .append(Component.text(" was killed by").color(NamedTextColor.YELLOW))
                .appendSpace()
                .append(killerName);
    }

    public KillMessage prefix(Component messagePrefix) {
        this.messagePrefix = messagePrefix.colorIfAbsent(NamedTextColor.YELLOW);
        return this;
    }

    public KillMessage suffix(Component messageSuffix) {
        this.messageSuffix = messageSuffix.colorIfAbsent(NamedTextColor.YELLOW);
        return this;
    }

}
