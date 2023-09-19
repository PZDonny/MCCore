package MCCore.listeners;

import MCCore.Core;
import MCCore.MongoUtils;
import MCCore.events.PlayerRemovedFromArenaEvent;
import MCCore.minigameAPI.MinigameHandler;
import MCCore.minigameAPI.arenaManager.ArenaManager;
import MCCore.utils.Disguise.DisguiseHandler;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuit implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        Chat.playerChatChannels.put(p.getUniqueId(), 0);
        ArenaManager.addPlayerToHashArena(p);
        if (Core.isMinigameEnabled()){
            p.teleport(Core.getInstance().getMinigameWaitingWorld().getSpawnLocation());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPostJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        if (!p.isOnline()) return;
        MinigameHandler handler = MinigameHandler.getInstance();
        if (handler != null){
            createPlayerData(e.getPlayer(), handler);
        }
        MongoUtils.cachePlayerSettings(p);
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
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Successfully generated MongoDB document for player " + ChatColor.YELLOW + p.getName());
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
        Chat.playerChatChannels.remove(p.getUniqueId());
        ArenaManager.removePlayerFromArena(p, true, PlayerRemovedFromArenaEvent.RemoveCause.DISCONNECT);
        DisguiseHandler.undisguisePlayer(p);
        MinigameHandler handler = MinigameHandler.getInstance();
        if (handler != null){
            handler.removePlayerFromCache(p);
        }
        MongoUtils.uncachePlayerSettings(p);
    }
}
