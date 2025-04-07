package net.donnypz.mccore.events;

import com.xxmicloxx.NoteBlockAPI.model.Song;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class SongReceivedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    Song song;
    String requestTag;

    public SongReceivedEvent(Song song, String requestTag){
        this.song = song;
        this.requestTag = requestTag;
    }

    public Song getSong(){
        return song;
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
