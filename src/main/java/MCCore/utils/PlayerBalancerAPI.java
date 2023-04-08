package MCCore.utils;

import MCCore.Core;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;

public class PlayerBalancerAPI {

    public static void connectPlayerToFallback(Player player){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect"); //SubChannel
        out.writeUTF("lobbies"); //Section in PlayerBalancer Bungee Plugin Config
        player.sendPluginMessage(Core.getInstance(), "playerbalancer:main", out.toByteArray());
    }
}
