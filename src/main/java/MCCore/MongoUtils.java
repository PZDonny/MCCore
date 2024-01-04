package MCCore;

import MCCore.events.MongoConnectedEvent;
import MCCore.minigameAPI.MinigameHandler;
import MCCore.utils.Items;
import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;


public class MongoUtils {
    //Minigames
    private static MongoDatabase db;

    private static MongoDatabase minigameDB;
    private static MongoClient client;

    private static MongoCollection<Document> settingsCollection;
    private static final HashMap<UUID, Document> settingsCache = new HashMap<>();


    private static boolean connected = false;
    public static void connectToMongo(String cstring, String mainDatabaseName, String playtestDatabaseName, String minigameDatabaseName) {
        if (connected){
            return;
        }
        new Thread(() ->{
            try{
                ConnectionString connectionString = new ConnectionString(cstring);
                MongoClientSettings settings = MongoClientSettings.builder()
                        .applyConnectionString(connectionString)
                        .serverApi(ServerApi.builder()
                                .version(ServerApiVersion.V1)
                                .build())
                        .build();
                client = MongoClients.create(settings);
                if (Core.isPlaytest()){
                    Bukkit.getConsoleSender().sendMessage(Core.prefix+ ChatColor.YELLOW+"Utilizing Playtest Database!");
                    db = client.getDatabase(playtestDatabaseName);
                }
                else{
                    Bukkit.getConsoleSender().sendMessage(Core.prefix+ChatColor.RED+"Utilizing Production Database!");
                    db = client.getDatabase(mainDatabaseName);
                }

                minigameDB = client.getDatabase(minigameDatabaseName);
                settingsCollection = db.getCollection("settings");

                Bukkit.getConsoleSender().sendMessage(Core.prefix+ChatColor.AQUA+"Successfully connected to"+ChatColor.GREEN+ " MongoDB!");
                connected = true;
                new BukkitRunnable() {
                    public void run() {
                        new MongoConnectedEvent(db, minigameDB).callEvent();
                    }
                }.runTask(Core.getInstance());
            } catch (MongoException e){
                connected = false;
                Bukkit.getConsoleSender().sendMessage(Core.prefix+ChatColor.RED+"There was an error connecting to the MongoDB Database!");
            }
        }).start();
    }

//Check Connection Status
    public static boolean isConnected(){
        return connected;
    }

    public static void disconnect(){
        client.close();
        connected = false;
    }

//Get Collection
    public static MongoCollection<Document> getCollection(String collectionName){
        if (!isConnected()){
            return null;
        }
        return db.getCollection(collectionName);
    }

    public static MongoCollection<Document> getSettingsCollection() {
        return settingsCollection;
    }

    public static void cachePlayerSettings(Player p){
        Document doc = settingsCollection.find(new Document("player", p.getUniqueId().toString())).first();
        if (doc != null){
            settingsCache.put(p.getUniqueId(), doc);
        }
    }

    public static void uncachePlayerSettings(OfflinePlayer p){
        settingsCache.remove(p.getUniqueId());
    }

    public static Document getPlayerSettings(Player p){
        return settingsCache.get(p.getUniqueId());
    }

    public static Document getOfflinePlayerSettings(OfflinePlayer p){
        return settingsCollection.find(new Document("player", p.getUniqueId().toString())).first();
    }



    //Get Collection From Minigame Database
    public static MongoCollection<Document> getMinigameDBCollection(String collectionName){
        return minigameDB.getCollection(collectionName);
    }

//Get Document From Collection
    public static Document getDocument(MongoCollection<Document> collection, String key, String value){
        return collection.find(new Document(key, value)).first();
    }

    public static boolean collectionHasDocument(MongoCollection<Document> collection, String key, Object value){
        for (Document doc : collection.find()){
            if (doc.getString(key).equals(value)) return true;
        }
        return false;
    }

    public static boolean collectionHasDocument(MongoCollection<Document> collection, Document document){
        for (Document doc : collection.find()){
            if (doc.equals(document)) return true;
        }
        return false;
    }

    public static void replacePlayerDocument(OfflinePlayer player, Document replaceDocument, MinigameHandler minigameHandler){
        new Thread(() ->{
            Bson filter = new Document("player", player.getUniqueId());
            minigameHandler.getPlayerCollection().replaceOne(filter, replaceDocument, new ReplaceOptions().upsert(true));
        }).start();

    }

//Update Player Many
    public static void updatePlayerMany(Map<String, Object> values, UUID playerUUID, MongoCollection<Document> collection, Map<UUID, Document> cacheMap){
        new BukkitRunnable(){
            public void run(){
                Document doc = getPlayerDocument(playerUUID, collection, cacheMap);
                if (doc == null) return;
                Document newValue = new Document();
                for (String value : values.keySet()){
                    if (doc.get(value).equals(values.get(value))) continue;
                    newValue.append(value, values.get(value));
                }
                if (newValue.isEmpty()) return;
                Bson updateOperation = new Document ("$set", newValue);
                collection.updateOne(new Document("player", playerUUID.toString()), updateOperation); //Update Doc

                if (cacheMap != null && Bukkit.getPlayer(playerUUID) != null && Bukkit.getPlayer(playerUUID).isOnline()){
                    for (String value : values.keySet()){
                        doc.replace(value, values.get(value));
                    }
                    cacheMap.replace(playerUUID, doc);
                }
            }
        }.runTaskAsynchronously(Core.getInstance());
    }

    public static void updatePlayerMany(Map<String, Object> values, OfflinePlayer player, MinigameHandler minigameHandler){
        new BukkitRunnable(){
            public void run(){
                Document doc = getPlayerDocument(player, minigameHandler);
                if (doc == null) return;
                Document newValue = new Document();
                for (String value : values.keySet()){
                    if (doc.get(value).equals(values.get(value))){
                        continue;
                    }
                    newValue.append(value, values.get(value));
                }
                if (newValue.isEmpty()){
                    return;
                }
                Bson updateOperation = new Document("$set", newValue);
                minigameHandler.getPlayerCollection().updateOne(new Document("player", player.getUniqueId().toString()), updateOperation); //Update Doc

                if (player.isOnline()){
                    for (String value : values.keySet()){
                        doc.replace(value, values.get(value));
                    }
                    minigameHandler.setPlayerCache((Player) player, doc);
                }
            }
        }.runTaskAsynchronously(Core.getInstance());
    }

    public static void updatePlayerManyWithDBDocument(Map<String, Object> values, OfflinePlayer player, MinigameHandler minigameHandler){
        new BukkitRunnable(){
            public void run(){
                Document doc = getPlayerDocumentFromDB(player.getUniqueId(), minigameHandler);
                if (doc == null) return;
                Document newValue = new Document();
                for (String value : values.keySet()){
                    if (doc.get(value).equals(values.get(value))){
                        continue;
                    }
                    newValue.append(value, values.get(value));
                }
                if (newValue.isEmpty()){
                    return;
                }
                Bson updateOperation = new Document("$set", newValue);
                minigameHandler.getPlayerCollection().updateOne(new Document("player", player.getUniqueId().toString()), updateOperation); //Update Doc

                if (player.isOnline()){
                    minigameHandler.setPlayerCache((Player) player, getPlayerDocumentFromDB(player.getUniqueId(), minigameHandler));
                }
            }
        }.runTaskAsynchronously(Core.getInstance());
    }

    public static void updatePlayerManyWithDBDocument(Map<String, Object> values, OfflinePlayer player, Document doc, MinigameHandler minigameHandler){
        new BukkitRunnable(){
            public void run(){
                if (doc == null) return;
                Document newValue = new Document();
                for (String value : values.keySet()){
                    if (doc.get(value).equals(values.get(value))){
                        continue;
                    }
                    newValue.append(value, values.get(value));
                }
                if (newValue.isEmpty()){
                    return;
                }
                Bson updateOperation = new Document("$set", newValue);
                minigameHandler.getPlayerCollection().updateOne(new Document("player", player.getUniqueId().toString()), updateOperation); //Update Doc

                if (player.isOnline()){
                    minigameHandler.setPlayerCache((Player) player, getPlayerDocumentFromDB(player.getUniqueId(), minigameHandler));
                }
            }
        }.runTaskAsynchronously(Core.getInstance());
    }

//Update Player Settings One

    public static void updatePlayerSettings(String mongoValue, Object updateValue, UUID playerUUID){
        updatePlayerOne(mongoValue, updateValue, playerUUID, settingsCollection, settingsCache);
    }

    public static void updatePlayerSettingsMany(Map<String, Object> values, UUID playerUUID){
        updatePlayerMany(values, playerUUID, settingsCollection, settingsCache);
    }

//Update Player One
    public static void updatePlayerOne(String mongoValue, Object updatedValue, UUID playerUUID, MongoCollection<Document> collection, Map<UUID, Document> cacheMap){
        new BukkitRunnable(){
            public void run(){
                Document doc = getPlayerDocument(playerUUID, collection, cacheMap);
                if (doc == null) return;
                if (doc.get(mongoValue).equals(updatedValue)) return;
                Document newValue = new Document(mongoValue, updatedValue); //New Values
                Bson updateOperation = new Document ("$set", newValue);
                collection.updateOne(new Document("player", playerUUID.toString()), updateOperation); //Update Doc

                if (cacheMap != null && Bukkit.getPlayer(playerUUID) != null && Bukkit.getPlayer(playerUUID).isOnline()){
                    doc.replace(mongoValue, updatedValue);
                    cacheMap.replace(playerUUID, doc);
                }
            }
        }.runTaskAsynchronously(Core.getInstance());
    }

    public static void updatePlayerOne(String mongoValue, Object updatedValue, OfflinePlayer player, MinigameHandler minigameHandler){
        new BukkitRunnable(){
            public void run(){
                Document doc = getPlayerDocument(player, minigameHandler);
                if (doc == null) return;
                if (doc.get(mongoValue).equals(updatedValue)) return;
                Document newValue = new Document(mongoValue, updatedValue); //New Values
                Bson updateOperation = new Document ("$set", newValue);
                minigameHandler.getPlayerCollection().updateOne(new Document("player", player.getUniqueId().toString()), updateOperation); //Update Doc

                if (player.isOnline()){
                    doc.replace(mongoValue, updatedValue);
                    minigameHandler.setPlayerCache((Player) player, doc);
                }
            }
        }.runTaskAsynchronously(Core.getInstance());
    }

//Update Sync
    public static Document updateOneSync(String mongoValue, Object updatedValue, Document document, MongoCollection<Document> collection){
        if (document == null) return null;
        Document newValue = new Document(mongoValue, updatedValue); //New Values
        Bson updateOperation = new Document ("$set", newValue);
        collection.updateOne(document, updateOperation); //Update Doc
        document.replace(mongoValue, updatedValue);
        return document;
    }

    public static Document updateManySync(Map<String, Object> values, Document document, MongoCollection<Document> collection){
        if (document == null) return null;
        Document newValue = new Document();
        for (String value : values.keySet()){
            newValue.append(value, values.get(value));
        }
        Bson updateOperation = new Document ("$set", newValue);
        collection.updateOne(document, updateOperation); //Update Doc

        for (String value : values.keySet()){
            document.replace(value, values.get(value));
        }
        return document;
    }



//Getting Player Documents
    public static Document getPlayerDocument(OfflinePlayer player, MinigameHandler minigameHandler){
        Document document;
        if (player == null) return null;
        if (player.isOnline()){
            document = minigameHandler.getPlayerCacheDocument((Player) player);
            if (document == null){
                document = minigameHandler.getPlayerCollection().find(new Document("player", player.getUniqueId().toString())).first();
            }
        }
        else{
            document = minigameHandler.getPlayerCollection().find(new Document("player", player.getUniqueId().toString())).first();
        }
        return document;
    }
    public static Document getPlayerDocument(UUID playerUUID, MongoCollection<Document> collection, Map<UUID, Document> cacheMap){
        Document document;
        Player p;
        p = Bukkit.getPlayer(playerUUID);
        if (p != null && p.isOnline()){
            document = cacheMap.get(playerUUID);
        }
        else{
            document = collection.find(new Document("player", playerUUID.toString())).first();
        }
        return document;
    }

    public static Document getPlayerDocument(OfflinePlayer player, MongoCollection<Document> collection, Map<UUID, Document> cacheMap){
        Document document;
        if (player == null) return null;
        if (player.isOnline()){
            document = cacheMap.get(player.getUniqueId());
        }
        else{
            document = collection.find(new Document("player", player.getUniqueId().toString())).first();
        }
        return document;
    }

    public static Document getPlayerDocumentFromDB(UUID playerUUID, MinigameHandler minigameHandler){
        return getPlayerDocumentFromDB(playerUUID, minigameHandler.getPlayerCollection());
    }

    public static Document getPlayerDocumentFromDB(UUID playerUUID, MongoCollection<Document> collection){
        return collection.find(new Document("player", playerUUID.toString())).first();
    }

    public static <T> List<T> getClonedMongoList(Document document, String listName, Class<T> classType){
        return new ArrayList<>(document.getList(listName, classType));
    }


//"Custom" Objects

    //Location Object To List
    public static ArrayList<Object> locationToList(Location location){
        ArrayList<Object> newLocation = new ArrayList<>();
        newLocation.add(location.getX());
        newLocation.add(location.getY());
        newLocation.add(location.getZ());
        newLocation.add(location.getPitch());
        newLocation.add(location.getYaw());
        newLocation.add(location.getWorld().getName());
        return newLocation;
    }


    //List to Location Object
    public static Location listToLocation(List<Object> list){
        if (list.size() != 6) return null;
        double x = (double) list.get(0);
        double y = (double) list.get(1);
        double z = (double) list.get(2);
        double pitch = (double) list.get(3);
        double yaw = (double) list.get(4);
        String worldName = (String) list.get(5);
        World world = Bukkit.getWorld(worldName);

        return new Location(world, x, y, z, (float) yaw, (float) pitch);
    }

    public static Location listToLocation(List<Object> list, World newWorld){
        if (list.size() != 6) return null;
        double x = (double) list.get(0);
        double y = (double) list.get(1);
        double z = (double) list.get(2);
        double pitch = (double) list.get(3);
        double yaw = (double) list.get(4);

        return new Location(newWorld, x, y, z, (float) yaw, (float) pitch);
    }

    public enum CurrencyType{
        SHARDS("shards", Items.makeItem(Material.PRISMARINE_SHARD, 1, ChatColor.AQUA+"Shards")),
        TOKENS("tokens", Items.makeItem(Material.SUNFLOWER, 1, ChatColor.GOLD+"Tokens")),
        COINS("coins", Items.makeItem(Material.HONEYCOMB, 1, ChatColor.AQUA+"Coins")),
        GEMS("gems", Items.makeItem(Material.LARGE_AMETHYST_BUD, 1, ChatColor.LIGHT_PURPLE+"Gems")),
        SOULS("souls", Items.makeItem(Material.SOUL_LANTERN, 1, ChatColor.DARK_AQUA+"Souls")),
        ENERGY("energy", Items.makeItem(Material.HEART_OF_THE_SEA, 1, ChatColor.AQUA+"Energy"));

        final String mongoKey;
        final ItemStack itemStack;

        CurrencyType(String mongoKey, ItemStack itemStack){
            this.mongoKey = mongoKey;
            this.itemStack = itemStack;
        }

        public String getMongoKey(){
            return mongoKey;
        }

        public String getMongoKeyCapitalized(){
            String first = mongoKey.substring(0, 1).toUpperCase();
            return first+mongoKey.substring(1);
        }

        public String getMongoKeyParenthesized(){
            if (mongoKey.charAt(mongoKey.length()-1) != 's'){
                return mongoKey;
            }
            String parenthesized = mongoKey.substring(0, mongoKey.length()-1)+"(";
            return parenthesized+mongoKey.charAt(mongoKey.length()-1)+")";
        }

        public String getDisplayName(){
            return itemStack.getItemMeta().getDisplayName();
        }

        public ItemStack getItemStack() {
            return itemStack;
        }
    }

    public enum CurrencyModifyType{
        ADD,
        SUBTRACT;
    }

    public static void updateCurrencyAsync(OfflinePlayer p, int changeAmount, CurrencyType currencyType, CurrencyModifyType modifyType, MongoCollection<Document> collection, Map<UUID, Document> cacheMap){
        new BukkitRunnable(){
            public void run(){
                Document doc = MongoUtils.getPlayerDocument(p, collection, cacheMap);
                int newCurrency = doc.getInteger(currencyType.mongoKey);
                if (modifyType == CurrencyModifyType.ADD){
                    newCurrency = newCurrency+changeAmount;
                }
                else if (modifyType == CurrencyModifyType.SUBTRACT){
                    newCurrency = newCurrency-changeAmount;
                }
                MongoUtils.updatePlayerOne(currencyType.mongoKey, newCurrency, p.getUniqueId(), collection, cacheMap);
            }
        }.runTaskAsynchronously(Core.getInstance());
    }

    public static int updateCurrency(OfflinePlayer p, int initialCurrency, int changeAmount, CurrencyType currencyType, CurrencyModifyType modifyType, MongoCollection<Document> collection, Map<UUID, Document> cacheMap){
        int newCurrency = initialCurrency;
        if (modifyType == CurrencyModifyType.ADD){
            newCurrency = newCurrency+changeAmount;
        }
        else if (modifyType == CurrencyModifyType.SUBTRACT){
            newCurrency = newCurrency-changeAmount;
        }
        MongoUtils.updatePlayerOne(currencyType.mongoKey, newCurrency, p.getUniqueId(), collection, cacheMap);
        return newCurrency;
    }

    public static void updateCurrencyAsync(OfflinePlayer p, int changeAmount, CurrencyType currencyType, CurrencyModifyType modifyType, MinigameHandler minigameHandler){
        new BukkitRunnable(){
            public void run(){
                Document doc = MongoUtils.getPlayerDocument(p, minigameHandler);
                int newCurrency = doc.getInteger(currencyType.mongoKey);
                if (modifyType == CurrencyModifyType.ADD){
                    newCurrency = newCurrency+changeAmount;
                }
                else if (modifyType == CurrencyModifyType.SUBTRACT){
                    newCurrency = newCurrency-changeAmount;
                }
                MongoUtils.updatePlayerOne(currencyType.mongoKey, newCurrency, p, minigameHandler);
            }
        }.runTaskAsynchronously(Core.getInstance());
    }

    public static int updateCurrency(OfflinePlayer p, int initialCurrency, int changeAmount, CurrencyType currencyType, CurrencyModifyType modifyType, MinigameHandler minigameHandler){
        int newCurrency = initialCurrency;
        if (modifyType == CurrencyModifyType.ADD){
            newCurrency = newCurrency+changeAmount;
        }
        else if (modifyType == CurrencyModifyType.SUBTRACT){
            newCurrency = newCurrency-changeAmount;
        }
        MongoUtils.updatePlayerOne(currencyType.mongoKey, newCurrency, p, minigameHandler);
        return newCurrency;
    }
}
