package MCCore.sockets;

import MCCore.Core;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.net.Socket;


public class ClientReconnector {

    private final String dataProxyIP;
    private final int port;
    private Client client;
    int attempt = 1;
    private static ClientReconnector instance;
    public ClientReconnector(String dataProxyIP, int port, Client client){
        this.dataProxyIP = dataProxyIP;
        this.port = port;
        this.client = client;
        instance = this;
        reconnect();
    }

    private void reconnect(){
        new Thread(() ->{
            while (true){
                try {
                    Thread.sleep(1000*15); //15s sleep
                    if (this.client == null || this.client.getSocket() == null || this.client.getSocket().isClosed()){
                        this.client = new Client(new Socket(dataProxyIP, port));
                        Core.setClient(this.client);
                        Bukkit.getConsoleSender().sendMessage(Component.text(Core.prefix+ChatColor.LIGHT_PURPLE+"Reconnected after "+attempt+" attempts!"));
                        attempt = 1;
                    }
                } catch (InterruptedException | IOException e) {
                    attempt++;
                    sendFailMessage();
                }
            }

        }).start();
    }

    private void sendFailMessage(){
        Bukkit.getConsoleSender().sendMessage(Component.text(Core.prefix+ ChatColor.RED+"Failed to reconnect to data proxy! Retrying! (Attempt #"+attempt+")"));
    }

    public static boolean hasInstance(){
        return instance != null;
    }


    public static boolean isConnected(){
        if (instance == null) return false;
        if (instance.client == null) return false;
        if (instance.client.getSocket() == null) return false;
        return !instance.client.getSocket().isClosed();
    }

}
