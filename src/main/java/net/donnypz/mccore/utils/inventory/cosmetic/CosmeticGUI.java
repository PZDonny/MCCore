package net.donnypz.mccore.utils.inventory.cosmetic;

import com.mongodb.client.MongoCollection;
import net.donnypz.mccore.utils.inventory.gui.ChestGUI;
import net.kyori.adventure.text.Component;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CosmeticGUI extends ChestGUI {

    final UUID playerUUID;
    final MongoCollection<Document> playerCollection;
    final MongoCollection<Document> unlockCollection;
    final String selectedField;
    String cosmeticTypeDisplayName;

    static final String COSMETIC_ID_FIELD = "cosmetic_id";

    public CosmeticGUI(int rows,
                       Component title,
                       @NotNull Player player,
                       @NotNull MongoCollection<Document> playerCollection,
                       @NotNull MongoCollection<Document> unlockCollection,
                       @NotNull String selectedField,
                       String cosmeticTypeDisplayName) {
        super(rows, title);
        this.playerUUID = player.getUniqueId();
        this.playerCollection = playerCollection;
        this.unlockCollection = unlockCollection;
        this.selectedField = selectedField;
        this.cosmeticTypeDisplayName = cosmeticTypeDisplayName;
    }
}
