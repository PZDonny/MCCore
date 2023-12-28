package MCCore.minigameAPI;

import MCCore.Core;
import MCCore.sockets.Client;
import MCCore.sockets.Messages;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayMinigame {
   static HashMap<UUID, Long> cooldowns = new HashMap<>();

    public static void join(Player p, String minigame, String mode, boolean cooldown){
        if (cooldown && cooldowns.containsKey(p.getUniqueId())){
            long timestamp = cooldowns.get(p.getUniqueId());
            if (timestamp > System.currentTimeMillis()){
                p.sendMessage(ChatColor.RED+"Please wait before doing that again!");
                return;
            }
            else{
                cooldowns.remove(p.getUniqueId());
            }
        }
        cooldowns.put(p.getUniqueId(), System.currentTimeMillis()+3000);
        p.sendMessage(ChatColor.GRAY+"Attempting to join minigame..");
        p.playSound(p, Sound.ENTITY_ALLAY_ITEM_THROWN, 1, 1.5f);
        Client client = Core.getClient();
        String[] out = new String[]{Messages.MINIGAMEAPI_JOINQUEUE.getID(), p.getUniqueId().toString(), minigame, mode};
        client.sendMessage(out);
    }
}
