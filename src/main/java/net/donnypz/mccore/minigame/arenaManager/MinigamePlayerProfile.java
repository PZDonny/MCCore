package net.donnypz.mccore.minigame.arenaManager;

import net.donnypz.mccore.utils.ui.scoreboard.PlayerScoreboard;
import net.donnypz.mccore.utils.ui.scoreboard.ScoreboardUtils;
import org.bson.Document;
import org.bukkit.OfflinePlayer;

public abstract class MinigamePlayerProfile {
    protected OfflinePlayer player;
    protected int kills = 0;
    protected int deaths = 0;
    private boolean isActive = true;

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

    public void setActive(boolean isActive){
        this.isActive = isActive;
    }

    public boolean isActive() {
        return isActive;
    }

    protected abstract void setCosmeticsData(Document document);

    public abstract void applyCosmetics();

    public int getKills(){
        return kills;
    }

    public int getDeaths(){
        return deaths;
    }

    public OfflinePlayer getPlayer(){
        return player;
    }

    public ArenaContainer getArenaContainer() {
        return arenaContainer;
    }

    public abstract void updatePlayerData();

    public abstract int getCurrencyEarned();


    void delete(){
        this.player = null;
    }
}
