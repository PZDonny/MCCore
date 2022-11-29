package core.ConnectDB;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.md_5.bungee.api.ChatColor;
import org.bson.Document;

public class coreMongo {
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
    coreMongo(){

    }
    public static void connectToMongo() {
        try(MongoClient client = MongoClients.create("mongodb+srv://admin:superjman7@minecraft.fzpkp.mongodb.net/?retryWrites=true&w=majority")){
            MongoDatabase db = client.getDatabase("skript");

            //Add collections
            collKit = db.getCollection("kitpvp");
            collKlash = db.getCollection("klash");
            collEvents = db.getCollection("events");
            //collFactions = db.getCollection("factions");
            collSettings = db.getCollection("settings");
            collFriends = db.getCollection("friends");
            collReport = db.getCollection("report");
            collParty = db.getCollection("party");
            collCosCrates = db.getCollection("cosmetics-crates");
            collCosHats = db.getCollection("cosmetics-hats");
            collCosOut = db.getCollection("cosmetics-outfits");
            collCosPets = db.getCollection("cosmetics-pets");
            collCosToys = db.getCollection("cosmetics-toys");

            System.out.println(ChatColor.AQUA + "Successfully connected to" + ChatColor.GREEN+ "MongoDB!");
        }
        //String uri = "mongodb+srv://admin:superjman7@minecraft.fzpkp.mongodb.net/?retryWrites=true&w=majority";


        //Document kit = collection.find(new Document("player", "d0bff3d3-22f7-478c-b5a3-173ad74c043b")).first();
        //System.out.println(kit.toJson());
    }
}
