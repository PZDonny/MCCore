package MCCore.minigameAPI.arenaManager;

import MCCore.utils.Scoreboard.PlayerScoreboard;
import MCCore.utils.Scoreboard.ScoreboardUtils;
import org.bukkit.OfflinePlayer;

public abstract class MinigamePlayerProfile {


    protected final OfflinePlayer player;

    protected int kills = 0;
    protected int deaths = 0;

    protected boolean isRespawning = false;
    protected final ArenaContainer arenaContainer;

    public MinigamePlayerProfile(OfflinePlayer player, ArenaContainer arenaContainer){
        this.player = player;
        this.arenaContainer = arenaContainer;
        arenaContainer.profiles.put(player.getUniqueId(), this);
    }

    public void addKill(){
        kills++;
        PlayerScoreboard board = ScoreboardUtils.getPlayerScoreboard(player.getUniqueId());
        PlayerScoreboard.UpdatingValue.PROFILE_KILLS.updateValue(board, this);
    }

    public void removeKill(){
        kills--;
        PlayerScoreboard board = ScoreboardUtils.getPlayerScoreboard(player.getUniqueId());
        PlayerScoreboard.UpdatingValue.PROFILE_KILLS.updateValue(board, this);
    }

    public void addDeath(){
        deaths++;
        PlayerScoreboard board = ScoreboardUtils.getPlayerScoreboard(player.getUniqueId());
        PlayerScoreboard.UpdatingValue.PROFILE_DEATHS.updateValue(board, this);
    }

    public void removeDeath(){
        deaths--;
        PlayerScoreboard board = ScoreboardUtils.getPlayerScoreboard(player.getUniqueId());
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

    public ArenaContainer getArenaContainer() {
        return arenaContainer;
    }

    public abstract int getShardsEarned();
}
