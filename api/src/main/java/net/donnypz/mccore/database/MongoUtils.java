package net.donnypz.mccore.database;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.ReplaceOptions;
import net.donnypz.mccore.events.MongoConnectedEvent;
import net.donnypz.mccore.utils.misc.RegionUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


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
        MongoDatabase existing = databases.get((databaseName));
        if (existing != null) return existing;

        MongoDatabase db = client.getDatabase(databaseName);
        Bukkit.getLogger().info("Registered Database ("+databaseName+")");
        databases.put(databaseName, db);
        return db;
    }

    public static boolean isConnected(){
        return connected;
    }

    /**
     * Disconnect from MongoDB, typically after a server shutdown or config reload
     */
    public static void disconnect(){
        if (client != null){
            client.close();
            client = null;
        }
        connected = false;
        databases.clear();
    }

    /**
     * Get the URI used to connect to MongoDB
     * @return a string
     */
    public static String getURI(){
        return uri;
    }

    public static MongoDatabase getDatabase(@NotNull String databaseName){
        if (!isConnected()){
            return null;
        }
        return databases.computeIfAbsent(databaseName, dbName -> client.getDatabase(databaseName));
    }

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
        DBExecutor.run(() -> {
            mongoPlayerCollection.replaceOne(filter, replaceDocument, new ReplaceOptions().upsert(true));
        });
    }

    static void update(@NotNull DatabaseUpdate databaseUpdate){
        if (databaseUpdate.isEmpty()) return;

        Document updateOperation = new Document();

        //Set Fields
        Document setDocument = new Document();
        for (Map.Entry<String, Object> entry : databaseUpdate.getSetValues().entrySet()){
            String field = entry.getKey();
            Object value = entry.getValue();
            setDocument.append(field, value);
        }
        if (!setDocument.isEmpty()) updateOperation.append("$set", setDocument);

        //Increment Fields
        Document incrementDocument = new Document();
        for (Map.Entry<String, NumberUpdate> entry : databaseUpdate.getIncrementedValues().entrySet()){
            String field = entry.getKey();
            NumberUpdate value = entry.getValue();
            switch (value.getNumberType()){
                case INT -> incrementDocument.append(field, value.intValue());
                case LONG -> incrementDocument.append(field, value.longValue());
                case DOUBLE -> incrementDocument.append(field, value.doubleValue());
                case FLOAT -> incrementDocument.append(field, value.floatValue());
            }
        }
        if (!incrementDocument.isEmpty()) updateOperation.append("$inc", incrementDocument);

        if (!updateOperation.isEmpty()){
            //Execute update on a separate thread
            DBExecutor.run(() -> {
                databaseUpdate
                        .getCollection()
                        .updateOne(databaseUpdate.getFilter(), updateOperation);
            });
        }
    }

    /**
     * Convert a list created with {@link  #locationToList(Location)} back to a {@link Location}
     * @param list the list with location properties
     * @return a location
     */
    public static Location listToLocation(@NotNull List<Object> list){
        return listToLocation(list, (String) list.get(5));
    }

    /**
     * Convert a list created with {@link  #locationToList(Location)} back to a {@link Location}
     * @param list the list with location properties
     * @param worldName the new world name
     * @return a location
     */
    public static Location listToLocation(@NotNull List<Object> list, @NotNull String worldName){
        return listToLocation(list, Bukkit.getWorld(worldName));
    }

    /**
     * Convert a list created with {@link  #locationToList(Location)} back to a {@link Location}
     * @param list the list with location properties
     * @param newWorld the world
     * @return a location
     */
    public static Location listToLocation(@NotNull List<Object> list, @NotNull World newWorld){
        double x = (double) list.get(0);
        double y = (double) list.get(1);
        double z = (double) list.get(2);
        double pitch = (double) list.get(3);
        double yaw = (double) list.get(4);
        return new Location(newWorld, x, y, z, (float) yaw, (float) pitch);
    }

    /**
     * Return a list containing properties of a location's x, y, z, pitch, yaw, and world name, respectively.
     * @param location
     * @return a list
     */
    public static List<Object> locationToList(@NotNull Location location){
        List<Object> list = new ArrayList<>();
        list.add(location.x());
        list.add(location.y());
        list.add(location.z());
        list.add(location.getPitch());
        list.add(location.getYaw());
        return list;
    }

    public static ArrayList<Object> locationBoundsToList(Player p) {
        Location[] loc = RegionUtils.getPlayerSelection(p);
        return loc.length == 0 ? null : locationBoundsToList(loc[0], loc[1]);
    }

    public static ArrayList<Object> locationBoundsToList(Location loc1, Location loc2) {
        ArrayList<Object> newLocation = new ArrayList();
        newLocation.add(loc1.getX());
        newLocation.add(loc1.getY());
        newLocation.add(loc1.getZ());
        newLocation.add(loc1.getPitch());
        newLocation.add(loc1.getYaw());
        newLocation.add(loc1.getWorld().getName());
        newLocation.add(loc2.getX());
        newLocation.add(loc2.getY());
        newLocation.add(loc2.getZ());
        newLocation.add(loc2.getPitch());
        newLocation.add(loc2.getYaw());
        newLocation.add(loc2.getWorld().getName());
        return newLocation;
    }

    public static Location[] listToLocationBounds(List<Object> list) {
        return list.size() != 12 ? null : listToLocationBounds(list, Bukkit.getWorld((String)list.get(5)));
    }

    public static Location[] listToLocationBounds(List<Object> list, World newWorld) {
        if (list.size() != 12) {
            return null;
        } else {
            double x1 = (double) list.get(0);
            double y1 = (double) list.get(1);
            double z1 = (double) list.get(2);
            double pitch1 = (double) list.get(3);
            double yaw1 = (double) list.get(4);
            Location loc1 = new Location(newWorld, x1, y1, z1, (float) yaw1, (float) pitch1);

            double x2 = (double) list.get(6);
            double y2 = (double) list.get(7);
            double z2 = (double) list.get(8);
            double pitch2 = (double) list.get(9);
            double yaw2 = (double) list.get(10);
            Location loc2 = new Location(newWorld, x2, y2, z2, (float) yaw2, (float) pitch2);
            return new Location[]{loc1, loc2};
        }
    }



    /*public static void addBinaryObjectToDocument(@NotNull Document document, @NotNull String key, @NotNull Serializable object){
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
    }*/
}
