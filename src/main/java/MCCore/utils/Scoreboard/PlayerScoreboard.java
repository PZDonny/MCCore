package MCCore.utils.Scoreboard;

import MCCore.minigameAPI.arenaManager.Arena;
import MCCore.minigameAPI.arenaManager.ArenaContainer;
import MCCore.minigameAPI.arenaManager.ArenaManager;
import MCCore.minigameAPI.arenaManager.MinigamePlayerProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class PlayerScoreboard {
    Player player;
    Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    Objective objective;
    Team playerTeam = scoreboard.registerNewTeam("1_player_team_psb");
    String header;
    String footer;
    Map<String, Integer> teamWeights = new HashMap<>();

    public PlayerScoreboard(@Nonnull Player player, @Nonnull String scoreboardTitle){
        this.player = player;
        PlayerScoreboard oldBoard = ScoreboardUtils.getPlayerScoreboard(player.getUniqueId());
        if (oldBoard != null){
            oldBoard.delete();
        }
        ScoreboardUtils.allBoards.put(player.getUniqueId(), this);
        objective = scoreboard.registerNewObjective("dummy", Criteria.DUMMY, Component.text(scoreboardTitle));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        playerTeam.addPlayer(player);
        playerTeam.setAllowFriendlyFire(false);
    }

    public PlayerScoreboard setHeader(String header){
        this.header = header;
        return this;
    }

    public PlayerScoreboard setFooter(String footer){
        this.footer = footer;
        return this;
    }

    public PlayerScoreboard setTeamColor(NamedTextColor color){
        playerTeam.color(color);
        return this;
    }

    public PlayerScoreboard resetTeamColor(){
        playerTeam.color(null);
        return this;
    }

    public PlayerScoreboard addTeammate(OfflinePlayer player){
        playerTeam.addPlayer(player);
        return this;
    }

    public PlayerScoreboard removeTeammate(OfflinePlayer player){
        playerTeam.removePlayer(player);
        return this;
    }

    public Team getPlayerTeam() {
        return playerTeam;
    }

    public PlayerScoreboard setPlayerTeamWeight(int weight){
        playerTeam = setTeamWeight(weight, playerTeam);
        return this;
    }


    public PlayerScoreboard createOtherTeam(String teamID){
        teamWeights.put(teamID, 2);
        scoreboard.registerNewTeam("2_"+teamID);
        return this;
    }

    public PlayerScoreboard addPlayerToOtherTeam(String teamID, OfflinePlayer player){
        Team otherTeam = scoreboard.getTeam(getOtherHiddenName(teamID));
        if (otherTeam != null){
            otherTeam.addPlayer(player);
        }
        return this;
    }

    public PlayerScoreboard removePlayerFromOtherTeam(String teamID, OfflinePlayer player){
        Team otherTeam = scoreboard.getTeam(getOtherHiddenName(teamID));
        if (otherTeam != null){
            otherTeam.removePlayer(player);
        }
        return this;
    }

    public PlayerScoreboard setOtherTeamWeight(String teamID, int weight){
        Team otherTeam = scoreboard.getTeam(getOtherHiddenName(teamID));
        if (otherTeam != null){
            setTeamWeight(weight, otherTeam);
        }
        return this;
    }

    private Team setTeamWeight(int weight, Team team){
        String teamName = getOtherRawName(team.getName());
        if (scoreboard.getTeam(weight+"_"+teamName) != null){
            return team;
        }
        Team copyTeam = scoreboard.registerNewTeam(weight+"_"+teamName);
        copyTeam.addEntries(team.getEntries());
        copyTeam.setOption(Team.Option.COLLISION_RULE, team.getOption(Team.Option.COLLISION_RULE));
        copyTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, team.getOption(Team.Option.NAME_TAG_VISIBILITY));
        copyTeam.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, team.getOption(Team.Option.DEATH_MESSAGE_VISIBILITY));
        copyTeam.setColor(team.getColor());
        copyTeam.setAllowFriendlyFire(team.allowFriendlyFire());
        copyTeam.setCanSeeFriendlyInvisibles(team.canSeeFriendlyInvisibles());
        copyTeam.prefix(team.prefix());
        copyTeam.suffix(team.suffix());
        team.unregister();
        teamWeights.replace(teamName, weight);
        System.out.println(scoreboard.getTeams());
        return copyTeam;
    }

    public Team getOtherTeam(String teamID){
        return scoreboard.getTeam(getOtherHiddenName(teamID));
    }

    //Hidden name for other teams
    private String getOtherRawName(String teamID){
        return teamID.split("_", 2)[1];
    }

    private String getOtherHiddenName(String teamID){
        return teamWeights.get(teamID)+"_"+teamID;
    }

    public PlayerScoreboard showHealth(DisplaySlot displaySlot){
         Objective objHP = scoreboard.getObjective("health_psb");
         if (objHP == null){
             if (displaySlot == null){
                 return this;
             }
             objHP = scoreboard.registerNewObjective("health_psb", Criteria.HEALTH, Component.text(ChatColor.RED+"❤"), RenderType.INTEGER);
         }

         if (displaySlot != null){
             objHP.setDisplaySlot(displaySlot);
         }
         else{
             objHP.setDisplaySlot(null);
             objHP.unregister();
         }
         return this;
    }

    public PlayerScoreboard setHealthRenderType(@Nonnull RenderType renderType){
        Objective objHP = scoreboard.getObjective("health_psb");
        if (objHP == null){
            return this;
        }
        objHP.setRenderType(renderType);
        return this;
    }



    public PlayerScoreboard setPermanentValue(int lineNumber, String text){
        Score score = objective.getScore(text);
        score.setScore(lineNumber);
        return this;
    }

    public PlayerScoreboard setUpdatingValue(int lineNumber, UpdatingValue updatingValue, String prefix){
        Team team = scoreboard.registerNewTeam(updatingValue.getId());
        team.addEntry(prefix);
        team.setSuffix(ChatColor.GRAY+"-");
        Score score = objective.getScore(prefix);
        score.setScore(lineNumber);
        return this;
    }

    public PlayerScoreboard setUpdatingValue(int lineNumber, UpdatingValue updatingValue, String prefix, String value){
        Team team = scoreboard.registerNewTeam(updatingValue.getId());
        team.addEntry(prefix);
        team.setSuffix(value);
        Score score = objective.getScore(prefix);
        score.setScore(lineNumber);
        return this;
    }

    public PlayerScoreboard setUpdatingValue(int lineNumber, String id, String prefix, String value){
        Team team = scoreboard.registerNewTeam(id+"_uv");
        team.addEntry(prefix);
        team.setSuffix(value);
        Score score = objective.getScore(prefix);
        score.setScore(lineNumber);
        return this;
    }
    

    public boolean hasUpdatingValue(String id){
        for (Team t : scoreboard.getTeams()){
            if (t.getName().equals(id+"_uv")){
                return true;
            }
        }
        return false;
    }

    public PlayerScoreboard updateValue(String id, String newValue){
        Team team;
        if (id.contains("_uv")){
            team = scoreboard.getTeam(id);
        }
        else{
            team = scoreboard.getTeam(id+"_uv");
        }
        if (team != null){
            team.setSuffix(newValue);
        }
        return this;
    }


    public void display(){
        player.setScoreboard(scoreboard);
        if (header != null){
            player.sendPlayerListHeader(Component.text(header));
        }
        if (footer != null){
            player.sendPlayerListFooter(Component.text(footer));
        }

    }


    public Player getPlayer() {
        return player;
    }

    public Scoreboard getBukkitScoreboard() {
        return scoreboard;
    }

    public void delete(){
        for (Team team: scoreboard.getTeams()){
            team.unregister();
        }

        for (Objective o : scoreboard.getObjectives()){
            o.unregister();
        }
        teamWeights.clear();
    }





    public enum UpdatingValue{
        PROFILE_KILLS(),
        PROFILE_DEATHS(),
        ARENA_PLAYINGPLAYERS(),
        ARENA_SPECTATINGPLAYERS(),
        ONLINE_PLAYERS();


        public String getId() {
            return name().toLowerCase()+"_uv";
        }


        public void updateValue(PlayerScoreboard playerScoreboard, Object object){
            if (playerScoreboard == null){
                return;
            }
            switch(this){
                case PROFILE_KILLS -> {
                    if (object instanceof MinigamePlayerProfile profile){
                        String value = String.valueOf(profile.getKills());
                        playerScoreboard.updateValue(getId(), ChatColor.YELLOW+value);
                    }
                }

                case PROFILE_DEATHS -> {
                    if (object instanceof MinigamePlayerProfile profile){
                        String value = String.valueOf(profile.getDeaths());
                        playerScoreboard.updateValue(getId(), ChatColor.YELLOW+value);
                    }
                }

                case ONLINE_PLAYERS -> {
                    String value = String.valueOf(Bukkit.getOnlinePlayers().size());
                    playerScoreboard.updateValue(getId(), ChatColor.YELLOW+value);
                }

                case ARENA_PLAYINGPLAYERS -> {
                    if (object instanceof Arena arena){
                        String value = String.valueOf(arena.getPlayingPlayers().size());
                        playerScoreboard.updateValue(getId(), ChatColor.YELLOW+value);
                    }
                }

                case ARENA_SPECTATINGPLAYERS -> {
                    if (object instanceof Arena arena){
                        String value = String.valueOf(arena.getSpectatingPlayers().size());
                        playerScoreboard.updateValue(getId(), ChatColor.YELLOW+value);
                    }
                }
            }
        }
    }
}
