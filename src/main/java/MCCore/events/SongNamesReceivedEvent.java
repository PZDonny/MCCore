package MCCore.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;

public class SongNamesReceivedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    ArrayList<String> songNames;
    String requestTag;

    public SongNamesReceivedEvent(ArrayList<String> songNames, String requestTag){
        this.songNames = songNames;
        this.requestTag = requestTag;
    }

    public ArrayList<String> getSongNames(){
        return songNames;
    }

    public String getRequestTag(){
        return requestTag;
    }



    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
