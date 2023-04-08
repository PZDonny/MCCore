package MCCore.sockets;

import org.bukkit.Bukkit;

import java.io.*;
import java.net.Socket;

public class Client {

    private Socket socket;

    private ObjectInputStream input;
    private ObjectOutputStream output;

    public Client(Socket socket) {
        try{
            this.socket = socket;
            this.output = new ObjectOutputStream(socket.getOutputStream());
            this.input = new ObjectInputStream(socket.getInputStream());
            listenForMessage();
            String[] out = new String[]{"connect_"+Bukkit.getServer().getPort()};
            //ByteArrayDataOutput out = ByteStreams.newDataOutput();
            //out.writeUTF("connect_"+Bukkit.getServer().getPort());
            sendMessage(out);
            //sendMessage(new String[]{"connect_"+Bukkit.getServer().getPort()});
        } catch (IOException e) {
            closeEverything();
        }
    }

    public void sendMessage(String[] message){
        try{
            output.writeObject(message);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void listenForMessage(){
        new Thread(() -> {
            while (socket.isConnected()){
                try{
                    String[] message = (String[]) input.readObject();
                    if (message[0].contains("minigameapi")) MinigameAPIMessages.run(message);

                } catch (IOException e) {
                    closeEverything();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void disconnect(){
        closeEverything();
    }

    private void closeEverything(){
        try{
            if (output != null) output.close();

            if (input != null) input.close();

            if (socket != null) socket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
