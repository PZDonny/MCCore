package MCCore.listeners;

import MCCore.Core;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class Command implements Listener {

    @EventHandler
    public void onCmd(PlayerCommandPreprocessEvent e){
        String cmd = e.getMessage().split(" ")[0];
        switch(cmd){
            case "buy", "csbuy" ->{
                if (!Core.isLobbyServer()){
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(ChatColor.RED+"You can only execute this command in the lobby!");
                }
            }
        }
    }
}
