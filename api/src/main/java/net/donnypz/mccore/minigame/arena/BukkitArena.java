package net.donnypz.mccore.minigame.arena;


import net.donnypz.mccore.events.PlayerRemovedFromArenaEvent;
import net.donnypz.mccore.minigame.ArenaState;
import net.donnypz.mccore.utils.slime.SlimeUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class BukkitArena extends Arena{

    World world;

    protected BukkitArena(@Nullable UUID queueUUID, OfflinePlayer host, @NotNull ArenaSettings arenaSettings) {
        super(queueUUID, host, arenaSettings);
    }

    @Override
    boolean containsPlayer(@NotNull Player player){
        if (player.getWorld().equals(world)){
            return true;
        }
        if (arenaState == ArenaState.CONNECTING && startPlayers.contains(player)){
            return true;
        }
        return spectators.contains(player) || playingPlayers.contains(player);
    }

    public void setWorld(@NotNull World world){
        if (SlimeUtils.isSlimeInstalled()){
            if (SlimeUtils.isSlimeWorld(world.getName())){
                return;
            }
        }
        this.world = world;
        this.setActive();
    }

    @Override
    public World getBukkitWorld(){
        if (world == null){
            return null;
        }
        return world;
    }

    @Override
    void onPlayerRemoval(Player player, PlayerRemovedFromArenaEvent.RemoveCause cause){}

    @Override
    void onArenaDeletion() {
        if (world != null){
            Bukkit.getConsoleSender().sendMessage(MiniMessage
                    .miniMessage()
                    .deserialize("<gold>Bukkit arena world match has finalized! (<aqua>"+world.getName()+"<gold>)"));
            world = null;
        }
    }
}
