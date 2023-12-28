package MCCore.listeners;

import MCCore.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.*;


public class PluginMessage implements PluginMessageListener {

    private static PluginMessage instance;

    public PluginMessage(){
        instance = this;
    }

    public static PluginMessage getInstance(){
        return instance;
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, Player player, @NotNull byte[] message) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
        if (channel.equals("mccore:changechat")){
            try {
                UUID uuid = UUID.fromString(in.readUTF());
                int chatChannel = in.readInt();
                PlayerUtils.setPlayerChatChannel(uuid, chatChannel);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
