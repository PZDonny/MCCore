package MCCore;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.md_5.bungee.api.ChatColor;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;

public class connectDB {
    //Minigames
    public static MongoCollection<Document> collKit; //KitPvP Collection
    public static MongoCollection<Document> collKlash; //Klash Collection
    public static MongoCollection<Document> collEvents; //Events Collection
    //public static MongoCollection<Document> collFactions; //Factions Collection

    //General
    public static MongoCollection<Document> collSettings; // Collection
    public static MongoCollection<Document> collFriends; // Collection
    public static MongoCollection<Document> collReport; // Collection
    public static MongoCollection<Document> collParty; // Collection

    //Cosmetics
    public static MongoCollection<Document> collCosCrates; // Collection
    public static MongoCollection<Document> collCosHats; // Collection
    public static MongoCollection<Document> collCosOut; // Collection
    public static MongoCollection<Document> collCosPets; // Collection
    public static MongoCollection<Document> collCosToys; // Collection
    protected static void connectToMongo(String connectionString) {
        try{
            MongoClient client = MongoClients.create(connectionString);
            MongoDatabase db = client.getDatabase("MineClassic");
            //Add collections
            collKit = db.getCollection("kitpvp");
            collKlash = db.getCollection("klash");
            collEvents = db.getCollection("events");
            //Core.collFactions = db.getCollection("factions");
            collSettings = db.getCollection("settings");
            collFriends = db.getCollection("friends");
            collReport = db.getCollection("report");
            collParty = db.getCollection("party");
            collCosCrates = db.getCollection("cosmetics-crates");
            collCosHats = db.getCollection("cosmetics-hats");
            collCosOut = db.getCollection("cosmetics-outfits");
            collCosPets = db.getCollection("cosmetics-pets");
            collCosToys = db.getCollection("cosmetics-toys");

            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Successfully connected to" + ChatColor.GREEN+ " MongoDB!");
        }catch (MongoException e){
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"There was an error connecting to the MongoDB Database!");
        }
    }

    public static void updateDoc(String key, Object value, MongoCollection<Document> collection, Document doc){
        Bson updatedValue = new Document(key, value);
        Bson updateOperation = new Document ("$set", updatedValue);
        collection.updateOne(doc,updateOperation);
        //return ChatColor.GREEN+"Successfully updated MongoDB Document "+ChatColor.YELLOW+doc+ChatColor.GREEN+" in collection "+collection;
    }

    /*public Document getDocFromColl(MongoCollection<Document> collection){
        return collection.find(new Document("player", "d0bff3d3-22f7-478c-b5a3-173ad74c043b")).first();
    }*/
}
