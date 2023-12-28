package MCCore.sockets;

import java.util.ArrayList;
import java.util.List;

public class SentMinigame {
    String name;
    String mode;
    int playingPlayers;
    int queuedPlayers;
    static final List<SentMinigame> allMinigames = new ArrayList<>();

    public SentMinigame(String minigame){
        name = minigame.split("_")[0];
        minigame = minigame.replace(name+"_", "");

        mode = minigame.split("`")[0];
        if (mode.equals("null")){
            mode = null;
        }
        minigame = minigame.replace(mode+"`", "");



        queuedPlayers = Integer.parseInt(minigame.split("~")[0]);
        minigame = minigame.replace(queuedPlayers+"~", "");

        playingPlayers = Integer.parseInt(minigame);


        SentMinigame existing = getMinigame(name, mode);
        if (existing != null){
            existing.playingPlayers = playingPlayers;
            existing.queuedPlayers = queuedPlayers;
            return;
        }
        allMinigames.add(this);
    }


    public String getName() {
        return name;
    }

    public String getMode() {
        return mode;
    }

    public int getPlayingPlayers() {
        return playingPlayers;
    }

    public int getQueuedPlayers() {
        return queuedPlayers;
    }

    public static List<SentMinigame> getAllMinigames(){
        return new ArrayList<>(allMinigames);
    }

    public static SentMinigame getNonQueueMinigame(String minigame){
        for (SentMinigame game : allMinigames){
            if (game.name.equals(minigame) && (game.mode == null)){
                return game;
            }
        }
        return null;
    }

    public static SentMinigame getMinigame(String minigame, String mode){
        if (mode == null || mode.isBlank()){
            return getNonQueueMinigame(minigame);
        }

        for (SentMinigame game : allMinigames){
            if (minigame.equals(game.name) && mode.equals(game.mode)){
                return game;
            }
        }
        return null;
    }

    public String toString(){
        return name+":"+mode+" queued_"+queuedPlayers+"playing_"+playingPlayers;
    }
}
