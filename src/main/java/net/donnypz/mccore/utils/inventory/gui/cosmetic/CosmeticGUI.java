package net.donnypz.mccore.utils.inventory.gui.cosmetic;

import com.mongodb.client.MongoCollection;
import net.donnypz.mccore.utils.inventory.gui.ChestGUI;
import net.kyori.adventure.text.Component;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class CosmeticGUI extends ChestGUI {

    final UUID playerUUID;
    final MongoCollection<Document> playerCollection;
    final MongoCollection<Document> unlockCollection;
    final String selectedField;
    String cosmeticTypeDisplayName;

    public CosmeticGUI(int rows,
                       @NotNull Component title,
                       @NotNull Player player,
                       @NotNull MongoCollection<Document> playerCollection,
                       @NotNull MongoCollection<Document> unlockCollection,
                       @NotNull String selectedField,
                       @NotNull String cosmeticTypeDisplayName) {
        super(rows, title);
        this.playerUUID = player.getUniqueId();
        this.playerCollection = playerCollection;
        this.unlockCollection = unlockCollection;
        this.selectedField = selectedField;
        this.cosmeticTypeDisplayName = cosmeticTypeDisplayName;
    }
}
