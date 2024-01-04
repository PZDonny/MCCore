package MCCore.minigameAPI;

import MCCore.Core;
import MCCore.MongoUtils;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MinigameHandler {
    MongoCollection<Document> playerCollection = null;
    MongoCollection<Document> worldCollection = null;
    Map<UUID, Document> playerCache = new ConcurrentHashMap<>();
    Document playerTemplateDocument;
    static ArrayList<MinigameHandler> handlers = new ArrayList<>();

    public MinigameHandler(){
        handlers.add(this);
    }

    public static ArrayList<MinigameHandler> getHandlers() {
        return new ArrayList<>(handlers);
    }

    public void setPlayerCollection(MongoCollection<Document> mongoCollection){
        playerCollection = mongoCollection;
    }

    public void setWorldCollection(MongoCollection<Document> mongoCollection){
        worldCollection = mongoCollection;
    }

    public void setPlayerCollection(String collectionName, int maxAttempts){
        new Thread(() -> {
            int attempt = 0;

            StringBuilder failBuilder = new StringBuilder();
            failBuilder.append(ChatColor.RED)
                    .append("Failed to retrieve MongoDB Collection, ")
                    .append(ChatColor.YELLOW)
                    .append(collectionName)
                    .append(ChatColor.RED)
                    .append("!");

            while(attempt < maxAttempts){
                try {
                    if (MongoUtils.isConnected()){
                        playerCollection = MongoUtils.getCollection(collectionName);
                        if (playerCollection == null){
                            failBuilder.append(ChatColor.YELLOW)
                            .append(" (Collection does not exist)");
                            Bukkit.getConsoleSender().sendMessage(failBuilder.toString());
                        }
                        Bukkit.getConsoleSender().sendMessage(Core.prefix+ChatColor.GREEN+"Retrieved MongoDB Collection "+ChatColor.YELLOW+collectionName);
                        return;
                    }
                    attempt++;
                    Thread.sleep(150); // 0.15 delay
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            Bukkit.getConsoleSender().sendMessage(failBuilder.toString());
        }).start();
    }

    public void setWorldCollection(String collectionName, int maxAttempts){
        new Thread(() -> {
            int attempt = 0;

            StringBuilder failBuilder = new StringBuilder();
            failBuilder.append(ChatColor.RED)
                    .append("Failed to retrieve MongoDB Collection, ")
                    .append(ChatColor.YELLOW)
                    .append(collectionName)
                    .append(ChatColor.RED)
                    .append("!");

            while(attempt < maxAttempts){
                try {
                    if (MongoUtils.isConnected()){
                        worldCollection = MongoUtils.getCollection(collectionName);
                        if (worldCollection == null){
                            failBuilder.append(ChatColor.DARK_GREEN)
                                    .append(" (Collection does not exist)");
                            Bukkit.getConsoleSender().sendMessage(failBuilder.toString());
                        }
                        else{
                            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN+"Retrieved MongoDB Collection "+ChatColor.YELLOW+collectionName);
                        }

                        return;
                    }
                    attempt++;
                    Thread.sleep(150); // 0.15 delay
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            Bukkit.getConsoleSender().sendMessage(failBuilder.toString());
        }).start();
    }


    public void setPlayerTemplateDocument(Document doc){
        this.playerTemplateDocument = doc;
    }

    public Document getPlayerTemplateDocument() {
        return playerTemplateDocument;
    }

    public MongoCollection<Document> getPlayerCollection(){
        return playerCollection;
    }

    public MongoCollection<Document> getWorldCollection(){
        return worldCollection;
    }


    public void setPlayerCache(Player player, Document doc){
        playerCache.put(player.getUniqueId(), doc);
    }

    public void removePlayerFromCache(OfflinePlayer player){
        playerCache.remove(player.getUniqueId());
    }

    public void removePlayerFromCache(UUID uuid){
        playerCache.remove(uuid);
    }

    public boolean isPlayerCached(OfflinePlayer player){
        return playerCache.containsKey(player.getUniqueId());
    }

    public Document getPlayerCacheDocument(Player p){
        return playerCache.get(p.getUniqueId());
    }
}
