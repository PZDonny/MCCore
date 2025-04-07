package net.donnypz.mccore.utils.ui.scoreboard;

import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public final class ScoreboardUtils {

    private ScoreboardUtils(){}

    static HashMap<UUID, PlayerScoreboard> allBoards = new HashMap<>();


    public static PlayerScoreboard getPlayerScoreboard(Player player){
        return getPlayerScoreboard(player.getUniqueId());
    }

    public static PlayerScoreboard getPlayerScoreboard(UUID playerUuid){
        return allBoards.get(playerUuid);
    }

    static void removePlayerScoreboard(UUID playerUUID){
        allBoards.remove(playerUUID);
    }

    public static String formatTime(int timeInSeconds){
        int minutes = (int) Math.floor((double) timeInSeconds/60);
        int seconds = timeInSeconds % 60;
        if (seconds < 10){
            return minutes+":0"+seconds;
        }
        return minutes+":"+seconds;
    }

    public static String getCurrentDateForLine(){
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date date = new Date();
        return formatter.format(date);
    }
}
