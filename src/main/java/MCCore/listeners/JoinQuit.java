package MCCore.listeners;

import MCCore.Core;
import MCCore.MongoUtils;
import MCCore.events.PlayerDocumentCreatedEvent;
import MCCore.events.PlayerRemovedFromArenaEvent;
import MCCore.minigameAPI.MinigameHandler;
import MCCore.minigameAPI.arenaManager.ArenaManager;
import MCCore.utils.Disguise.DisguiseHandler;
import MCCore.utils.PlayerUtils;
import MCCore.utils.RankUtils;
import MCCore.utils.Scoreboard.PlayerScoreboard;
import MCCore.utils.Scoreboard.ScoreboardUtils;
import net.kyori.adventure.text.Component;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class JoinQuit implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        if (Core.isMongoAllowed()){
            if (!MongoUtils.isConnected()){
                p.kick(Component.text(ChatColor.RED+"Server has not completed startup!\nWait a moment before joining again!"));
                return;
            }
            for (MinigameHandler handler : MinigameHandler.getHandlers()){
                createPlayerData(p, handler);
            }
            MongoUtils.cachePlayerSettings(p);
        }

        PlayerUtils.setPlayerChatChannel(p, 0);
        if (Core.isMinigameEnabled()){
            e.joinMessage(null);
            p.teleport(Core.getInstance().getMinigameWaitingWorld().getSpawnLocation());
            ArenaManager.addPlayerToTargetArena(p);
        }
    }

    private void createPlayerData(Player p, MinigameHandler handler){
        new Thread(()->{
            Document existing = handler.getPlayerCollection().find(new Document("player", p.getUniqueId().toString())).first();
            Document templateDocument = handler.getPlayerTemplateDocument();
            if (existing == null){
                Document newDoc = new Document(templateDocument);
                newDoc.append("player", p.getUniqueId().toString());
                handler.getPlayerCollection().insertOne(newDoc);
                if (p.isOnline()){
                    handler.setPlayerCache(p, newDoc);
                }
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Successfully generated Settings MongoDB document for player " + ChatColor.YELLOW + p.getName());
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        new PlayerDocumentCreatedEvent(p, newDoc, handler.getPlayerCollection()).callEvent();
                    }
                }.runTask(Core.getInstance());
                return;
            }

            if (existing.getInteger("version") < templateDocument.getInteger("version")){
                for (String key : templateDocument.keySet()){
                    if (existing.containsKey(key)){
                        continue;
                    }
                    existing.put(key, templateDocument.get(key));
                }
                existing.replace("version", templateDocument.get("version"));
                MongoUtils.replacePlayerDocument(p, existing, handler);
            }
            handler.setPlayerCache(p, existing);
        }).start();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        PlayerUtils.unsetPlayerChatChannel(p);
        Chat.removeCooldown(p);
        ArenaManager.removePlayerFromArena(p, true, PlayerRemovedFromArenaEvent.RemoveCause.DISCONNECT);
        PlayerScoreboard scoreboard = ScoreboardUtils.getPlayerScoreboard(p.getUniqueId());
        if (scoreboard != null){
            scoreboard.delete();
        }
        DisguiseHandler.undisguisePlayer(p);
        for (MinigameHandler handler : MinigameHandler.getHandlers()){
            handler.removePlayerFromCache(p);
        }
        MongoUtils.uncachePlayerSettings(p);
        RankUtils.removeCachedPlayer(p);
        if (Core.isMinigameEnabled()){
            e.quitMessage(null);
        }
    }
}
