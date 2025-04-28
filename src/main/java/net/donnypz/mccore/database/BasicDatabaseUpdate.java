package net.donnypz.mccore.database;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

public final class BasicDatabaseUpdate extends DatabaseUpdate {
    public BasicDatabaseUpdate(@NotNull MongoCollection<Document> collection, @NotNull Document filter){
        super(collection, filter);
    }
}
