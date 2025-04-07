package net.donnypz.mccore.utils.inventory.cosmetic;

import com.mongodb.client.MongoCollection;
import net.donnypz.mccore.cosmetics.Cosmetic;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

public class DocumentCountCondition {
    MongoCollection<Document> collection;
    String playerUUIDField;
    int count;
    CountType type;
    String conditionDisplayName;

    public DocumentCountCondition(@NotNull MongoCollection<Document> collection, int count, @NotNull String playerUUIDField, @NotNull CountType type, @NotNull String conditionDisplayName) {
        this.collection = collection;
        this.playerUUIDField = playerUUIDField;
        this.count = count;
        this.type = type;
        this.conditionDisplayName = conditionDisplayName;
    }


    public DocumentCountCondition addCosmetic(Cosmetic cosmetic){
        cosmetic.addDocumentCountCondition(this);
        return this;
    }

    public MongoCollection<Document> getCollection(){
        return collection;
    }

    public int getCount() {
        return count;
    }

    public CountType getType(){
        return type;
    }

    public String conditionDisplayName() {
        return conditionDisplayName;
    }

    public String getPlayerUUIDField() {
        return playerUUIDField;
    }

    public enum CountType{
        AT_LEAST,
        AT_MOST,
        EXACT
    }
}
