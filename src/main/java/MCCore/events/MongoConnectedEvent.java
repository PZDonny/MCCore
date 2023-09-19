package MCCore.events;

import com.mongodb.client.MongoDatabase;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class MongoConnectedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    MongoDatabase mainDatabase;
    MongoDatabase minigameDatabase;

    public MongoConnectedEvent(MongoDatabase mainDatabase, MongoDatabase minigameDatabase){
        this.mainDatabase = mainDatabase;
        this.minigameDatabase = minigameDatabase;
    }


    public MongoDatabase getMainDatabase() {
        return mainDatabase;
    }

    public MongoDatabase getMinigameDatabase() {
        return minigameDatabase;
    }


    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
