package net.donnypz.mccore.cosmetics.conditions;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CountOptions;
import net.donnypz.mccore.cosmetics.Cosmetic;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DocumentCountCondition implements CosmeticCondition{
    MongoCollection<Document> collection;
    String playerUUIDField;
    int minimum;
    String displayName;

    public DocumentCountCondition(@NotNull MongoCollection<Document> collection, int minimum, @NotNull String playerUUIDField, @NotNull String displayName) {
        this.collection = collection;
        this.playerUUIDField = playerUUIDField;
        this.minimum = minimum;
        this.displayName = displayName;
    }


    public MongoCollection<Document> getCollection(){
        return collection;
    }

    public int getMinimum() {
        return minimum;
    }

    public String displayName() {
        return displayName;
    }

    public String getPlayerUUIDField() {
        return playerUUIDField;
    }

    @Override
    public boolean meetsCondition(Document document, UUID playerUUID) {
        Document filter = new Document(playerUUIDField, playerUUID.toString());
        long result = collection.countDocuments(filter, new CountOptions().limit(minimum));

        return result >= minimum;
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
