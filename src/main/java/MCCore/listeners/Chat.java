package MCCore.listeners;

import MCCore.Core;
import MCCore.utils.PlayerUtils;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Chat implements Listener {
    YamlConfiguration filter = YamlConfiguration.loadConfiguration(new InputStreamReader(Core.getInstance().getResource("chatfilter.yml")));

    final Set<String> strictWords = new HashSet<>();
    final Set<String> softWords = new HashSet<>();
    private static final HashMap<UUID, Long> cooldown = new HashMap<>();


    public Chat(){
        strictWords.addAll(filter.getStringList("strict"));
        softWords.addAll(filter.getStringList("soft"));
    }



    public static void removeCooldown(Player p){
        cooldown.remove(p.getUniqueId());
    }

    private static boolean isPlayerOnCooldown(Player p){
        return cooldown.containsKey(p.getUniqueId()) && System.currentTimeMillis() < cooldown.get(p.getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerChat(AsyncPlayerChatEvent e){
        Player p = e.getPlayer();
        int chatChannel = PlayerUtils.getPlayerChatChannel(p);
        String eventMessage = e.getMessage();

    //Party Chat
        if (chatChannel == 1) {
            e.setCancelled(true);
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF(p.getUniqueId().toString());
            out.writeUTF(eventMessage);
            p.sendPluginMessage(Core.getInstance(), "mccore:partychat", out.toByteArray());
        }


    //Local Chat
        else{
            if (Core.isChatCooldownEnabled()){
                if (isPlayerOnCooldown(p)){
                    p.sendMessage(ChatColor.RED+"Please wait 3 seconds before sending another message.");
                    p.sendMessage(ChatColor.GRAY+""+ChatColor.ITALIC+"Purchase Iron rank from our server store to remove chat delay (/buy)");
                    e.setCancelled(true);
                    return;
                }

                if (p.hasPermission("mc.chatdelay")){
                    cooldown.put(p.getUniqueId(), System.currentTimeMillis()+3000);
                }
            }
            StringBuilder builder = new StringBuilder();
            String[] message = eventMessage.split(" ");

        //Chat Filter
        filtering:
            for (int i = 0; i < message.length; i++){
                String word = message[i];
                if (word.length() >= 3){
                    String decodedWord = word
                            .replaceAll("[^0134a-zA-z@$\\s]|[\\^`\\[\\]_\\\\]", "")
                            .replace("0", "o")
                            .replace("1", "i")
                            .replace("3", "e")
                            .replace("4", "a");

                    //Hard/Strict Filter
                    for (String strictWord : strictWords){
                        if (decodedWord.toLowerCase().contains(strictWord)){
                            builder.append("*** ");
                            continue filtering;
                        }
                    }

                    //Soft Filter
                    for (String softWord : softWords){
                        if (decodedWord.equalsIgnoreCase(softWord)){
                            builder.append("*** ");
                            continue filtering;
                        }
                    }

                    builder.append(word)
                            .append(" ");
                }
                else{
                    builder.append(word).append(" ");
                }
            }
            e.setMessage(builder.toString());

            //Minigame Waiting World
            if (Core.isMinigameEnabled() && !Core.isLobbyServer()){
                World w = p.getWorld();
                if (w.equals(Core.getInstance().getMinigameWaitingWorld())){
                    e.getRecipients().clear();
                    e.getRecipients().addAll(w.getPlayers());
                    e.setFormat(p.getDisplayName()+ChatColor.GOLD+""+ChatColor.BOLD+" >> "+ChatColor.WHITE+e.getMessage());
                    //e.setFormat(prefix+color+p.getDisplayName()+ChatColor.GOLD+""+ChatColor.BOLD+" >> "+color+e.getMessage());
                }
            }
        }
    }
}
