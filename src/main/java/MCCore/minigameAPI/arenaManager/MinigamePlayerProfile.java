package MCCore.minigameAPI.arenaManager;

import MCCore.utils.Scoreboard.PlayerScoreboard;
import MCCore.utils.Scoreboard.ScoreboardUtils;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.UUID;

public abstract class MinigamePlayerProfile {

    private static final HashMap<UUID, MinigamePlayerProfile> allProfiles = new HashMap<>();

    protected final OfflinePlayer player;

    protected int kills = 0;
    protected int deaths = 0;

    protected boolean isRespawning = false;

    public MinigamePlayerProfile(OfflinePlayer player){
        this.player = player;
        allProfiles.put(player.getUniqueId(), this);
    }

    public static MinigamePlayerProfile getPlayerProfile(OfflinePlayer p){
        return allProfiles.get(p.getUniqueId());
    }

    void deleteProfile(){
        allProfiles.remove(player.getUniqueId());
    }

    public void addKill(){
        kills++;
        PlayerScoreboard board = ScoreboardUtils.getPlayerScoreboard(player.getUniqueId());
        PlayerScoreboard.UpdatingValue.PROFILE_KILLS.updateValue(board, this);
        PlayerScoreboard.UpdatingValue.PROFILE_KILLS.updateValue(board, this);
    }

    public void removeKill(){
        kills--;
        PlayerScoreboard board = ScoreboardUtils.getPlayerScoreboard(player.getUniqueId());
        PlayerScoreboard.UpdatingValue.PROFILE_KILLS.updateValue(board, this);
        PlayerScoreboard.UpdatingValue.PROFILE_KILLS.updateValue(board, this);
    }

    public void addDeath(){
        deaths++;
        PlayerScoreboard board = ScoreboardUtils.getPlayerScoreboard(player.getUniqueId());
        PlayerScoreboard.UpdatingValue.PROFILE_DEATHS.updateValue(board, this);
        PlayerScoreboard.UpdatingValue.PROFILE_DEATHS.updateValue(board, this);
    }

    public void removeDeath(){
        deaths--;
        PlayerScoreboard board = ScoreboardUtils.getPlayerScoreboard(player.getUniqueId());
        PlayerScoreboard.UpdatingValue.PROFILE_DEATHS.updateValue(board, this);
        PlayerScoreboard.UpdatingValue.PROFILE_DEATHS.updateValue(board, this);
    }

    void setRespawningState(boolean isRespawning){
        this.isRespawning = isRespawning;
    }

    public int getKills(){
        return kills;
    }

    public int getDeaths(){
        return deaths;
    }

    public boolean isRespawning() {
        return isRespawning;
    }

    public OfflinePlayer getPlayer(){
        return player;
    }

    public abstract int getShardsEarned();
}
