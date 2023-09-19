package MCCore.listeners;

import MCCore.Core;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Chat implements Listener {

    static Map<UUID, Integer> playerChatChannels = new ConcurrentHashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerChat(AsyncPlayerChatEvent e){
        Player p = e.getPlayer();
        int chatChannel = playerChatChannels.get(p.getUniqueId());

    //Party Chat
        if (chatChannel == 1) {
            e.setCancelled(true);
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF(p.getUniqueId().toString());
            out.writeUTF(e.getMessage());
            p.sendPluginMessage(Core.getInstance(), "mccore:partychat", out.toByteArray());
        }
    }
}
