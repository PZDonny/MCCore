package net.donnypz.mccore.events;

import net.donnypz.mccore.database.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerDataCachedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    Player player;
    PlayerData playerData;

    public PlayerDataCachedEvent(Player player, PlayerData playerData){
        super(!Bukkit.isPrimaryThread());
        this.player = player;
        this.playerData = playerData;
    }

    public Player getPlayer() {
        return player;
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
