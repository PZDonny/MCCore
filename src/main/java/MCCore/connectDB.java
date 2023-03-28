package MCCore;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;


public class connectDB {
    //Minigames
    public static MongoCollection<Document> collKit; //KitPvP Collection



    private static boolean connected = false;
    static void connectToMongo(String cstring) {
        try{
            ConnectionString connectionString = new ConnectionString(cstring);
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .serverApi(ServerApi.builder()
                            .version(ServerApiVersion.V1)
                            .build())
                    .build();
            MongoClient client = MongoClients.create(settings);
            MongoDatabase db;
            if (Core.isPlaytest){
                Bukkit.getConsoleSender().sendMessage(Core.prefix+ ChatColor.YELLOW+"Utilizing Playtest Database!");
                db = client.getDatabase("MineClassicPlaytest");
            }
            else{
                Bukkit.getConsoleSender().sendMessage(Core.prefix+ChatColor.RED+"Utilizing Production Database!");
                db = client.getDatabase("MineClassic");
            }
            //Add collections
            collKit = db.getCollection("kitpvp");

            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA+"Successfully connected to"+ChatColor.GREEN+ " MongoDB!");
            connected = true;
        }catch (MongoException e){
            connected = false;
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"There was an error connecting to the MongoDB Database!");
        }
    }

    public static boolean isConnected(){
        return connected;
    }

    public static void updateDoc(String key, Object value, MongoCollection<Document> collection, Document doc){
        Bson updatedValue = new Document(key, value);
        Bson updateOperation = new Document ("$set", updatedValue);
        collection.updateOne(doc,updateOperation);
        //return ChatColor.GREEN+"Successfully updated MongoDB Document "+ChatColor.YELLOW+doc+ChatColor.GREEN+" in collection "+collection;
    }

}
