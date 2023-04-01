package MCCore.minigameAPI;

import MCCore.Core;
import MCCore.sockets.Client;
import org.bukkit.entity.Player;

public class MinigameJoin {

    public static void attempt(Player p, String minigame, String mode){
        Client client = Core.getClient();
        String[] out = new String[]{"minigameapi:joinqueue", p.getUniqueId().toString(), minigame, mode};
        client.sendMessage(out);
    }
}
