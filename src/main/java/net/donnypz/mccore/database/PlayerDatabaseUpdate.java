package net.donnypz.mccore.database;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class PlayerDatabaseUpdate extends DatabaseUpdate {

    public PlayerDatabaseUpdate(@NotNull MongoCollection<Document> collection, @NotNull UUID playerUUID){
        super(collection, new Document("uuid", playerUUID.toString()));
    }
}
