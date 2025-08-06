package net.donnypz.mccore.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An event to notify depending plugins when a connection to MongoDB to established
 */
public class MongoConnectedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();


    public MongoConnectedEvent(){}


    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
