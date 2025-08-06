package net.donnypz.mccore.events;

import net.donnypz.mccore.database.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class PlayerDataUncachedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    UUID playerUUID;
    PlayerData playerData;

    public PlayerDataUncachedEvent(UUID playerUUID, PlayerData playerData){
        super(!Bukkit.isPrimaryThread());
        this.playerUUID = playerUUID;
        this.playerData = playerData;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
