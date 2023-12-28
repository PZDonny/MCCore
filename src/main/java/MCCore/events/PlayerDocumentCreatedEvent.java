package MCCore.events;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerDocumentCreatedEvent extends Event {


    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Document document;
    private final MongoCollection<Document> collection;

    public PlayerDocumentCreatedEvent(Player player, Document document, MongoCollection<Document> collection){
        this.player = player;
        this.document = document;
        this.collection = collection;
    }

    public Player getPlayer() {
        return player;
    }

    public Document getDocument() {
        return document;
    }

    public MongoCollection<Document> getCollection(){
        return collection;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
