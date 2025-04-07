package net.donnypz.mccore.events;

import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerMongoDocumentCreatedEvent extends Event {


    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Document document;

    public PlayerMongoDocumentCreatedEvent(Player player, Document document){
        this.player = player;
        this.document = document;
    }

    public Player getPlayer() {
        return player;
    }

    public Document getDocument() {
        return document;
    }


    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
