package net.donnypz.mccore.database;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Sorts;
import net.donnypz.mccore.events.MongoConnectedEvent;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.Binary;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public final class MongoUtils {
    private static final ConcurrentHashMap<String, MongoDatabase> databases = new ConcurrentHashMap<>();
    private static MongoClient client;
    private static String uri;
    private static boolean connected = false;

    private MongoUtils(){}

    public static void createConnection(@NotNull String connectionString) {
        uri = connectionString;
        if (connected) return;

        try{
            ServerApi api = ServerApi.builder()
                    .version(ServerApiVersion.V1)
                    .build();

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(connectionString))
                    .serverApi(api)
                    .build();

            client = MongoClients.create(settings);
            Bukkit.getLogger().info("MongoDB Client Created");
            connected = true;

            new MongoConnectedEvent().callEvent(); //Let depending plugin(s) know that MongoDB has connected
        } catch (MongoException e){
            Bukkit.getLogger().severe("There was an error creating the MongoDB Client");
            connected = false;
        }
    }

    public static MongoDatabase registerDatabase(@NotNull String databaseName){
        MongoDatabase db = client.getDatabase(databaseName);
        Bukkit.getLogger().info("Registered Database ("+databaseName+")");
        databases.put(databaseName, db);
        return db;
    }

    public static boolean isConnected(){
        return connected;
    }

    public static void disconnect(){
        if (client != null){
            client.close();
            client = null;
        }
        connected = false;
        databases.clear();
    }

    public static String getURI(){
        return uri;
    }

    public static MongoDatabase getDatabase(@NotNull String databaseName){
        if (!isConnected()){
            return null;
        }
        return databases.computeIfAbsent(databaseName, dbName -> client.getDatabase(databaseName));
    }

    //Get Collection
    public static MongoCollection<Document> getCollection(@NotNull String collectionName, @NotNull MongoDatabase mongoDatabase){
        if (!isConnected()){
            return null;
        }
        return mongoDatabase.getCollection(collectionName);
    }

    public static Document getDocument(@NotNull MongoCollection<Document> collection, @NotNull String field, @NotNull String value){
        return collection.find(new Document(field, value)).first();
    }

    public static void replacePlayerDBDocument(@NotNull UUID playerUUID, @NotNull Document replaceDocument, @NotNull MongoCollection<Document> mongoPlayerCollection){
        Bson filter = new Document("uuid", playerUUID.toString());
        ExecutorRequest.run(() -> {
            mongoPlayerCollection.replaceOne(filter, replaceDocument, new ReplaceOptions().upsert(true));
        });
    }

    static void update(@NotNull DatabaseUpdate databaseUpdate){
        update(databaseUpdate.getSetValues(), databaseUpdate.getIncrementedValues(), databaseUpdate.getFilter(), databaseUpdate.getCollection());
    }

    static void update(Map<String, Object> setValues, Map<String, NumberUpdate> incrementValues, Document filter, MongoCollection<Document> collection){
        Document updateOperation = new Document();
        if (setValues != null && !setValues.isEmpty()){
            Document setDocument = new Document();
            for (String key : setValues.keySet()){
                setDocument.append(key, setValues.get(key));
            }
            updateOperation.append("$set", setDocument);
        }

        if (incrementValues != null && !incrementValues.isEmpty()){
            Document incrementDocument = new Document();
            for (String key : incrementValues.keySet()){
                incrementDocument.append(key, incrementValues.get(key).intValue());
            }
            updateOperation.append("$inc", incrementDocument);
        }

        if (updateOperation.isEmpty()){
            return;
        }

        //Execute update on a separate thread
        ExecutorRequest.run(() -> {
            collection.updateOne(filter, updateOperation);
        });
    }

    public static void addBinaryObjectToDocument(@NotNull Document document, @NotNull String key, @NotNull Serializable object){
        try(ByteArrayOutputStream byteOut = new ByteArrayOutputStream()){
            ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
            objOut.writeObject(object);
            byte[] data = byteOut.toByteArray();
            document.append(key, data);
            objOut.close();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
        finally{

        }
    }

    public static Object getBinaryObjectFromDocument(@NotNull Document document, @NotNull String key){
        byte[] bytes = ((Binary) document.get(key)).getData();
        try(ByteArrayInputStream in = new ByteArrayInputStream(bytes)){
            ObjectInputStream objIn = new ObjectInputStream(in);
            Object obj = objIn.readObject();
            objIn.close();
            return obj;
        }
        catch(IOException | ClassNotFoundException ex){
            ex.printStackTrace();
            return null;
        }
    }


    public static List<Document> getSortedDocuments(@NotNull String fieldName, @NotNull MongoCollection<Document> collection, boolean isAscending){
        return getSortedDocuments(fieldName, collection, isAscending, -1);
    }

    public static List<Document> getSortedDocuments(@NotNull String fieldName, @NotNull MongoCollection<Document> collection, boolean isAscending, int limit){
        Bson sortOrder = isAscending ? Sorts.ascending(fieldName) : Sorts.descending(fieldName);

        FindIterable<Document> iter = collection
                .find()
                .sort(sortOrder);

        if (limit >= 0){
            iter.limit(limit);
        }

        return StreamSupport.stream(iter.spliterator(), false)
                .collect(Collectors.toList());
    }
}
