package net.donnypz.mccore.database;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * A builder class for preparing an update to a database, using a player's UUID to filter effected documents
 */
public final class PlayerDatabaseUpdate extends DatabaseUpdate {

    public PlayerDatabaseUpdate(@NotNull MongoCollection<Document> collection, @NotNull UUID playerUUID){
        super(collection, new Document("uuid", playerUUID.toString()));
    }

    public PlayerDatabaseUpdate(@NotNull MongoCollection<Document> collection, @NotNull UUID playerUUID, @NotNull String uuidField){
        super(collection, new Document(uuidField, playerUUID.toString()));
    }
}
