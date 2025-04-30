package net.donnypz.mccore.database.cosmeticConditions;

import net.donnypz.mccore.cosmetics.Cosmetic;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PermissionCondition implements CosmeticCondition{

    String permission;
    Component displayName;

    public PermissionCondition(@NotNull String permission, @NotNull Component displayName){
        this.permission = permission;
        this.displayName = displayName;
    }

    public String getPermission() {
        return permission;
    }

    public Component getDisplayName() {
        return displayName;
    }

    @Override
    public boolean meetsCondition(Document document, UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return false;
        return player.hasPermission(permission);
    }

    @Override
    public Component buildLore(Cosmetic cosmetic) {
        return Component.text("Requires: ", NamedTextColor.GRAY)
                .append(displayName)
                .append(Component.space())
                .append(Component.text("permission(s)", NamedTextColor.GRAY))
                .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }
}
