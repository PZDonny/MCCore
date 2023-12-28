package MCCore.utils.Scoreboard;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public final class ScoreboardUtils {

    private ScoreboardUtils(){}

    static HashMap<UUID, PlayerScoreboard> allBoards = new HashMap<>();


    public static PlayerScoreboard getPlayerScoreboard(Player player){
        return getPlayerScoreboard(player.getUniqueId());
    }

    public static PlayerScoreboard getPlayerScoreboard(UUID uuid){
        return allBoards.get(uuid);
    }

    public static String formatTime(int timeInSeconds){
        int minutes = (int) Math.floor((double) timeInSeconds/60);
        int seconds = timeInSeconds % 60;
        if (seconds < 10){
            return minutes+":0"+seconds;
        }
        return minutes+":"+seconds;
    }
}
