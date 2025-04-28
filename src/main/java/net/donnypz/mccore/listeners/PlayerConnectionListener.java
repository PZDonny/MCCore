package net.donnypz.mccore.listeners;

import net.donnypz.mccore.Core;
import net.donnypz.mccore.database.MongoUtils;
import net.donnypz.mccore.database.PlayerData;
import net.donnypz.mccore.events.*;
import net.donnypz.mccore.minigame.arena.ArenaManager;
import net.donnypz.mccore.utils.misc.RankUtils;
import net.donnypz.mccore.utils.ui.actionbar.ActionBarUtils;
import net.donnypz.mccore.utils.ui.scoreboard.PlayerScoreboard;
import net.donnypz.mccore.utils.ui.scoreboard.ScoreboardUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreLogin(AsyncPlayerPreLoginEvent e){
        if ((Core.isMongoEnabled() && !MongoUtils.isConnected())){
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Component.text("Server has not completed startup!\nWait a moment before joining again!", NamedTextColor.RED));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        p.displayName(p.displayName().color(RankUtils.getPlayerNamedTextColor(p)));

        if (Core.isConnectionMessageHidden()){
            e.joinMessage(null);
        }

        if (Core.isMinigameEnabled()){
            if (Core.isWaitingWorldLimited()){
                p.teleport(Core.getInstance().getMinigameWaitingWorld().getSpawnLocation());
            }
        }

        for (Player o : Bukkit.getOnlinePlayers()){
            PlayerScoreboard board = ScoreboardUtils.getPlayerScoreboard(o);
            PlayerScoreboard.UpdatingValue.ONLINE_PLAYERS.updateValue(board, null);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();

        //Remove Cached Player Data
        PlayerData.remove(e.getPlayer());

        ChatListener.removeCooldown(p);
        ActionBarUtils.removePlayerCancel(p);
        ArenaManager.removePlayerFromArena(p, PlayerRemovedFromArenaEvent.RemoveCause.DISCONNECT);

        PlayerScoreboard scoreboard = ScoreboardUtils.getPlayerScoreboard(p.getUniqueId());
        if (scoreboard != null){
            scoreboard.delete();
        }


        if (Core.isConnectionMessageHidden()){
            e.quitMessage(null);
        }

        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
            for (Player o : Bukkit.getOnlinePlayers()){
                PlayerScoreboard board = ScoreboardUtils.getPlayerScoreboard(o);
                PlayerScoreboard.UpdatingValue.ONLINE_PLAYERS.updateValue(board, null);
            }
        }, 1);
    }
}
