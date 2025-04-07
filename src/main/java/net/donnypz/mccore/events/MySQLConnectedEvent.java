package net.donnypz.mccore.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MySQLConnectedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    public MySQLConnectedEvent(){}

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
