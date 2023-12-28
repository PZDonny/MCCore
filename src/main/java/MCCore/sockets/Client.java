package MCCore.sockets;

import MCCore.Core;
import MCCore.events.SongNamesReceivedEvent;
import MCCore.events.SongReceivedEvent;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Client {

    private Socket socket;

    private ObjectInputStream input;
    private ObjectOutputStream output;

    Timer keepAliveTimer = new Timer();

    public Client(String dataProxyIP, int port) {
        new Thread(()->{
            try{
                this.socket = new Socket(dataProxyIP, port);
                this.output = new ObjectOutputStream(socket.getOutputStream());
                this.input = new ObjectInputStream(socket.getInputStream());
                listenForMessage();
                String serverType;
                if (Core.isLobbyServer()){
                    serverType = "spigot_lobby";
                }
                else{
                    serverType = "spigot";
                }
                String[] out = new String[]{serverType, Core.getServerID(), String.valueOf(Bukkit.getServer().getPort())};
                sendMessage(out);
                sendPlayerCount();
                Bukkit.getConsoleSender().sendMessage(new TextComponent(Core.prefix+ ChatColor.GREEN+"Successfully connected to data proxy!"));

                if (!ClientReconnector.hasInstance()){
                    new ClientReconnector(Core.getDataProxyIP(), Core.getPort(), this);
                }
            } catch (IOException e) {
                if (!ClientReconnector.hasInstance()){
                    new ClientReconnector(Core.getDataProxyIP(), Core.getPort(), null);
                }
                Bukkit.getConsoleSender().sendMessage(Core.prefix+ ChatColor.RED+"Failed to connect to data proxy!");
                Bukkit.getConsoleSender().sendMessage(Core.prefix+"Reconnecting...");
                closeEverything();
            }
        }).start();
    }

    Client(Socket socket) {
        try{
            this.socket = socket;
            this.output = new ObjectOutputStream(socket.getOutputStream());
            this.input = new ObjectInputStream(socket.getInputStream());
            listenForMessage();
            String serverType;
            if (Core.isLobbyServer()){
                serverType = "spigot_lobby";
            }
            else{
                serverType = "spigot";
            }
            String[] out = new String[]{serverType, Core.getServerID(), String.valueOf(Bukkit.getServer().getPort())};
            sendMessage(out);
            sendPlayerCount();
            Bukkit.getConsoleSender().sendMessage(new TextComponent(Core.prefix+ ChatColor.GREEN+"Successfully connected to data proxy!"));
            if (!ClientReconnector.hasInstance()) new ClientReconnector(Core.getDataProxyIP(), Core.getPort(), this);
        } catch (IOException e) {
            if (!ClientReconnector.hasInstance()) new ClientReconnector(Core.getDataProxyIP(), Core.getPort(), null);
            Bukkit.getConsoleSender().sendMessage(Core.prefix+ ChatColor.RED+"Failed to connect to data proxy!");
            Bukkit.getConsoleSender().sendMessage(Core.prefix+"Reconnecting...");
            closeEverything();
        }
    }

    //Keep alive and Player Count Updater
    private void sendPlayerCount(){
        keepAliveTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!socket.isConnected() || socket.isClosed()){
                    cancel();
                    return;
                }
                sendMessage(new String[]{Messages.SPIGOT_PLAYERCOUNT.getID(), String.valueOf(Bukkit.getOnlinePlayers().size())});
            }
        },0, 3000);
    }

    public boolean isSocketConnected(){
        return socket.isConnected();
    }

    public Socket getSocket(){
        return this.socket;
    }

    public void sendMessage(String[] message){
        if (socket == null || socket.isClosed() || output == null) return;
        try{
            output.writeObject(message);
            output.flush();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void listenForMessage(){
        new Thread(() -> {
            while (socket.isConnected()){
                if (socket.isClosed()){
                    disconnect();
                    return;
                }
                try{
                    Object[] objectArray = (Object[]) input.readObject();

                //String Arrays
                    if (objectArray instanceof String[] message){
                        if (message[0].contains("mgapi")){

                        }
                        else if (message[0].equalsIgnoreCase(Messages.DISCONNECT.getID())){
                            disconnect();
                        }
                    }


                //Object Arrays
                    else {
                        String tag = (String) objectArray[0];
                        if (tag.contains("mgapi")){
                            MinigameAPIMessages.run(objectArray);
                        }
                        if (tag.equals(Messages.NBAPI_GETSONGNAMES.getID())){
                            ArrayList<String> songNames = (ArrayList<String>) objectArray[1];
                            String requestTag = (String) objectArray[2];
                            new BukkitRunnable(){
                                public void run(){
                                    Bukkit.getPluginManager().callEvent(new SongNamesReceivedEvent(songNames, requestTag));
                                }
                            }.runTask(Core.getInstance());
                        }
                        else if (tag.equals(Messages.NBAPI_GETSONG.getID())){
                            byte[] bytes =(byte[]) objectArray[1];
                            String requestTag = (String) objectArray[2];
                            DataInputStream byteSong = new DataInputStream(new ByteArrayInputStream(bytes));
                            Song song = NBSDecoder.parse(byteSong);
                            new BukkitRunnable(){
                                public void run(){
                                    Bukkit.getPluginManager().callEvent(new SongReceivedEvent(song, requestTag));
                                }
                            }.runTask(Core.getInstance());
                        }
                        else if (tag.equalsIgnoreCase(Messages.NETWORK_DATA.getID())){
                            int players = (int) objectArray[1];
                            Core.setNetworkPlayerCount(players);
                            for (int i = 2; i < objectArray.length; i++){
                                new SentMinigame((String) objectArray[i]);
                            }
                        }
                    }
                } catch (IOException e) {
                    disconnect();
                    return;
                } catch (ClassNotFoundException e) {
                    Bukkit.getConsoleSender().sendMessage(Component.text(Core.prefix+ChatColor.RED+"An error when attempting to receive data!"));
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void disconnect(){
        Bukkit.getConsoleSender().sendMessage(Component.text(Core.prefix+ChatColor.YELLOW+"Closing connection with data proxy!"));
        closeEverything();
    }


    private void closeEverything(){
        keepAliveTimer.cancel();
        try{
            if (socket != null){
                socket.close();
            }

            if (output != null){
                output.close();
            }

            if (input != null){
                input.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
