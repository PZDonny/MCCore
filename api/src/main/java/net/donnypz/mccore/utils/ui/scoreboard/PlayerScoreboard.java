package net.donnypz.mccore.utils.ui.scoreboard;

import net.donnypz.mccore.minigame.arena.Arena;
import net.donnypz.mccore.minigame.arena.MinigamePlayerProfile;
import net.donnypz.mccore.version.CoreAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class PlayerScoreboard {
    UUID player;
    Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    Objective objective;
    Objective tablistObjective;
    UpdatingValue trackedTablistUpdatingValue = null;
    Team playerTeam = scoreboard.registerNewTeam("1_player_team_psb");
    Team spectatorTeam = scoreboard.registerNewTeam("9_spectator_team_psb");
    Component header;
    Component footer;
    Map<String, Integer> teamWeights = new HashMap<>();
    String nonSidebarEntry = "+nonsb";

    public PlayerScoreboard(@NotNull Player player, @NotNull Component scoreboardTitle){
        this.player = player.getUniqueId();
        PlayerScoreboard oldBoard = ScoreboardUtils.getPlayerScoreboard(player.getUniqueId());
        if (oldBoard != null){
            oldBoard.delete();
        }
        ScoreboardUtils.allBoards.put(player.getUniqueId(), this);
        objective = scoreboard.registerNewObjective("dummy", Criteria.DUMMY, scoreboardTitle);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        tablistObjective = scoreboard.registerNewObjective("dummy_tablist", Criteria.DUMMY, (Component) null, RenderType.INTEGER);
        playerTeam.addPlayer(player);
        playerTeam.setAllowFriendlyFire(false);

        spectatorTeam.color(NamedTextColor.GRAY);
        spectatorTeam.setAllowFriendlyFire(false);
        spectatorTeam.setCanSeeFriendlyInvisibles(true);
    }

    public PlayerScoreboard setHeader(Component header){
        this.header = header;
        return this;
    }

    public PlayerScoreboard setFooter(Component footer){
        this.footer = footer;
        return this;
    }

    public PlayerScoreboard setTeamColor(NamedTextColor color){
        playerTeam.color(color);
        return this;
    }


    public PlayerScoreboard showScoreboardNumbers(){
        CoreAPI.getVersionHandler().showScoreboardNumbers(objective);
        return this;
    }

    public PlayerScoreboard hideScoreboardNumbers(){
        CoreAPI.getVersionHandler().hideScoreboardNumbers(objective);
        return this;
    }


    public PlayerScoreboard resetTeamColor(){
        playerTeam.color(null);
        return this;
    }

    public PlayerScoreboard setYellowNumberToProfileKills(){
        tablistObjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        trackedTablistUpdatingValue = UpdatingValue.PROFILE_KILLS;
        return this;
    }

    public PlayerScoreboard setYellowNumberToProfileDeaths(){
        tablistObjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        trackedTablistUpdatingValue = UpdatingValue.PROFILE_DEATHS;
        return this;
    }

    public PlayerScoreboard unsetYellowNumber(){
        tablistObjective.setDisplaySlot(null);
        trackedTablistUpdatingValue = null;
        return this;
    }

    public PlayerScoreboard updateYellowNumberOfProfilePlayer(MinigamePlayerProfile profile){
        OfflinePlayer player = profile.getPlayer();

        switch (trackedTablistUpdatingValue){
            case PROFILE_DEATHS -> {
                tablistObjective.getScore(player).setScore(profile.getDeaths());
            }
            case PROFILE_KILLS -> {
                tablistObjective.getScore(player).setScore(profile.getKills());
            }
        }
        return this;
    }

    public PlayerScoreboard updateYellowNumberOfProfilePlayer(MinigamePlayerProfile profile, Arena arena){
        for (Player p : arena.getArenaPlayers()){
            PlayerScoreboard sb = ScoreboardUtils.getPlayerScoreboard(p);
            if (sb == null){
                continue;
            }
            sb.updateYellowNumberOfProfilePlayer(profile);
        }
        return this;
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

    public PlayerScoreboard setHealthRenderType(@NotNull RenderType renderType){
        Objective objHP = scoreboard.getObjective("health_psb");
        if (objHP == null){
            return this;
        }
        objHP.setRenderType(renderType);
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

    public PlayerScoreboard addSpectator(OfflinePlayer spectator){
        spectatorTeam.addPlayer(spectator);
        return this;
    }

    public PlayerScoreboard removeSpectator(OfflinePlayer spectator, boolean addToPlayerTeam){
        if (addToPlayerTeam){
            playerTeam.addPlayer(spectator); //auto removes from other teams
        }
        else{
            spectatorTeam.removePlayer(spectator);
        }
        return this;
    }

    public PlayerScoreboard removeSpectator(OfflinePlayer spectator, @NotNull String teamToJoin){
        spectatorTeam.removePlayer(spectator);
        getOtherTeam(teamToJoin).addPlayer(spectator);
        return this;
    }

    public PlayerScoreboard removeSpectator(OfflinePlayer spectator, @NotNull Team teamToJoin){
        spectatorTeam.removePlayer(spectator);
        teamToJoin.addPlayer(spectator);
        return this;
    }

    public Team getPlayerTeam() {
        return playerTeam;
    }

    public Team getSpectatorTeam() {
        return spectatorTeam;
    }

    public String getPlayerTeamName(){
        return playerTeam.getName();
    }

    public String getSpectatorTeamName(){
        return spectatorTeam.getName();
    }

    public PlayerScoreboard setPlayerTeamWeight(int weight){
        playerTeam = setTeamWeight(weight, playerTeam);
        return this;
    }

    public PlayerScoreboard setSpectatorTeamWeight(int weight){
        playerTeam = setTeamWeight(weight, spectatorTeam);
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

    public PlayerScoreboard addEntityToOtherTeam(String teamID, Entity entity){
        if (entity instanceof OfflinePlayer p){
            addPlayerToOtherTeam(teamID, p);
        }
        if (entity.isDead()){
            return this;
        }
        Team otherTeam = scoreboard.getTeam(getOtherHiddenName(teamID));
        if (otherTeam != null){
            otherTeam.addEntity(entity);
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



    public PlayerScoreboard setPermanentValue(int lineNumber, String text){
        Score score = objective.getScore(text);
        score.setScore(lineNumber);
        return this;
    }

    public PlayerScoreboard registerNonSidebarUpdatingValue(UpdatingValue updatingValue){
        scoreboard.registerNewTeam(updatingValue.getId());
        return this;
    }

    public PlayerScoreboard setUpdatingValue(int lineNumber, UpdatingValue updatingValue, String prefix){
        Team team = scoreboard.registerNewTeam(updatingValue.getId());
        team.addEntry(prefix);
        team.suffix(Component.text("-", NamedTextColor.GRAY));
        Score score = objective.getScore(prefix);
        score.setScore(lineNumber);
        return this;
    }

    public PlayerScoreboard setUpdatingValue(int lineNumber, UpdatingValue updatingValue, String prefix, Component value){
        return setUpdatingValue(lineNumber, updatingValue.getId(), prefix, value);
    }

    public PlayerScoreboard setUpdatingValue(int lineNumber, String id, String prefix, Component value){
        if (!id.endsWith("_uv")){
            id = id+"_uv";
        }
        Team team = scoreboard.registerNewTeam(id);
        team.addEntry(prefix);
        team.suffix(value);
        Score score = objective.getScore(prefix);
        score.setScore(lineNumber);
        return this;
    }

    public PlayerScoreboard unsetUpdatingValue(String id){
        if (!id.endsWith("_uv")){
            id = id+"_uv";
        }
        Team team = scoreboard.getTeam(id);
        if (team == null){
            return this;
        }

        if (!team.getEntries().isEmpty()){
            for (String entry : new HashSet<>(team.getEntries())){
                objective.getScore(entry).resetScore();
                team.removeEntry(entry);
            }
        }
        team.unregister();
        return this;
    }

    public PlayerScoreboard editUpdatingValue(int lineNumber, String id, String prefix){
        if (!id.endsWith("_uv")){
            id = id+"_uv";
        }
        Team team = scoreboard.getTeam(id);
        if (team == null){
            team = scoreboard.registerNewTeam(id);
        }
        if (!team.getEntries().isEmpty()){
            for (String entry : new HashSet<>(team.getEntries())){
                objective.getScore(entry).resetScore();
                team.removeEntry(entry);
            }
        }
        team.addEntry(prefix);
        Score score = objective.getScore(prefix);
        score.setScore(lineNumber);
        return this;
    }

    public PlayerScoreboard editUpdatingValue(int lineNumber, UpdatingValue updatingValue, String prefix){
        return editUpdatingValue(lineNumber, updatingValue.getId(), prefix);
    }

    public PlayerScoreboard editUpdatingValuePrefix(String id, String prefix){
        if (!id.endsWith("_uv")){
            id = id+"_uv";
        }
        Team team = scoreboard.getTeam(id);
        if (team == null){
            team = scoreboard.registerNewTeam(id);
        }

        int lastScore = 0;
        if (!team.getEntries().isEmpty()){
            for (String entry : new HashSet<>(team.getEntries())){
                Score oldScore = objective.getScore(entry);
                lastScore = oldScore.getScore();
                oldScore.resetScore();
                team.removeEntry(entry);

            }
        }

        team.addEntry(prefix);
        Score score = objective.getScore(prefix);
        score.setScore(lastScore);
        return this;
    }

    public PlayerScoreboard editUpdatingValuePrefix(UpdatingValue updatingValue, String prefix) {
        return editUpdatingValuePrefix(updatingValue.getId(), prefix);
    }
    

    public boolean hasUpdatingValue(String id){
        if (!id.endsWith("_uv")){
            id = id+"_uv";
        }
        return scoreboard.getTeam(id) != null;
    }

    public PlayerScoreboard updateValue(String id, Component newValue){
        Team team;
        if (id.endsWith("_uv")){
            team = scoreboard.getTeam(id);
        }
        else{
            team = scoreboard.getTeam(id+"_uv");
        }
        if (team != null){
            team.suffix(newValue);
        }
        return this;
    }


    public void display(){
        Player p = Bukkit.getPlayer(player);
        if (p == null){
            return;
        }
        p.setScoreboard(scoreboard);
        if (header != null){
            p.sendPlayerListHeader(header);
        }
        if (footer != null){
            p.sendPlayerListFooter(footer);
        }

    }


    public Player getPlayer() {
        return Bukkit.getPlayer(player);
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

        ScoreboardUtils.removePlayerScoreboard(player);
    }





    public enum UpdatingValue{
        PROFILE_KILLS,
        PROFILE_DEATHS,
        ARENA_PLAYINGPLAYERS,
        ARENA_SPECTATINGPLAYERS,
        ARENA_SPECTATINGPLAYERS_STARTING,
        ARENA_SPECTATINGPLAYERS_OUTSIDE,
        ARENA_SPECTATINGPLAYERS_PROFILED,
        ARENA_SPECTATINGPLAYERS_PROFILE_ACTIVE,
        ARENA_SPECTATINGPLAYERS_PROFILE_INACTIVE,
        ARENA_SPECTATINGPLAYERS_PROFILELESS_AND_INACTIVE,
        ARENA_SPECTATINGPLAYERS_PROFILELESS,
        ARENA_TIMELEFT,
        ONLINE_PLAYERS;


        public String getId() {
            return name().toLowerCase()+"_uv";
        }


        public void updateValue(PlayerScoreboard playerScoreboard, Object object){
            if (playerScoreboard == null || !playerScoreboard.hasUpdatingValue(getId())){
                return;
            }
            switch(this){
                case PROFILE_KILLS -> {
                    if (object instanceof MinigamePlayerProfile profile){
                        String value = String.valueOf(profile.getKills());
                        playerScoreboard.updateValue(getId(), Component.text(value, NamedTextColor.YELLOW));
                    //Yellow Number
                        if (playerScoreboard.trackedTablistUpdatingValue == this){
                            playerScoreboard.updateYellowNumberOfProfilePlayer(profile, profile.getArenaContainer().getArena());
                        }
                    }
                }

                case PROFILE_DEATHS -> {
                    if (object instanceof MinigamePlayerProfile profile){
                        String value = String.valueOf(profile.getDeaths());
                        playerScoreboard.updateValue(getId(), Component.text(value, NamedTextColor.YELLOW));
                    //Yellow Number
                        if (playerScoreboard.trackedTablistUpdatingValue == this){
                            playerScoreboard.updateYellowNumberOfProfilePlayer(profile, profile.getArenaContainer().getArena());
                        }
                    }
                }

                case ONLINE_PLAYERS -> {
                    String value = String.valueOf(Bukkit.getOnlinePlayers().size());
                    playerScoreboard.updateValue(getId(), Component.text(value, NamedTextColor.YELLOW));
                }

                case ARENA_PLAYINGPLAYERS -> {
                    if (object instanceof Arena arena){
                        String value = String.valueOf(arena.getPlayingPlayers().size());
                        playerScoreboard.updateValue(getId(), Component.text(value, NamedTextColor.YELLOW));
                    }
                }

                case ARENA_SPECTATINGPLAYERS -> {
                    if (object instanceof Arena arena){
                        String value = String.valueOf(arena.getSpectatingPlayers(Arena.SpectatorType.ALL).size());
                        playerScoreboard.updateValue(getId(), Component.text(value, NamedTextColor.YELLOW));
                    }
                }
                case ARENA_SPECTATINGPLAYERS_OUTSIDE -> {
                    if (object instanceof Arena arena){
                        String value = String.valueOf(arena.getSpectatingPlayers(Arena.SpectatorType.OUTSIDE_PLAYER).size());
                        playerScoreboard.updateValue(getId(), Component.text(value, NamedTextColor.YELLOW));
                    }
                }
                case ARENA_SPECTATINGPLAYERS_STARTING -> {
                    if (object instanceof Arena arena){
                        String value = String.valueOf(arena.getSpectatingPlayers(Arena.SpectatorType.STARTING_PLAYER).size());
                        playerScoreboard.updateValue(getId(), Component.text(value, NamedTextColor.YELLOW));
                    }
                }
                case ARENA_SPECTATINGPLAYERS_PROFILED -> {
                    if (object instanceof Arena arena){
                        String value = String.valueOf(arena.getSpectatingPlayers(Arena.SpectatorType.PROFILED_PLAYER).size());
                        playerScoreboard.updateValue(getId(), Component.text(value, NamedTextColor.YELLOW));
                    }
                }
                case ARENA_SPECTATINGPLAYERS_PROFILE_ACTIVE -> {
                    if (object instanceof Arena arena){
                        String value = String.valueOf(arena.getSpectatingPlayers(Arena.SpectatorType.PROFILE_ACTIVE_PLAYER).size());
                        playerScoreboard.updateValue(getId(), Component.text(value, NamedTextColor.YELLOW));
                    }
                }
                case ARENA_SPECTATINGPLAYERS_PROFILE_INACTIVE -> {
                    if (object instanceof Arena arena){
                        String value = String.valueOf(arena.getSpectatingPlayers(Arena.SpectatorType.PROFILE_INACTIVE_PLAYER).size());
                        playerScoreboard.updateValue(getId(), Component.text(value, NamedTextColor.YELLOW));
                    }
                }

                case ARENA_SPECTATINGPLAYERS_PROFILELESS_AND_INACTIVE -> {
                    if (object instanceof Arena arena){
                        String value = String.valueOf(arena.getSpectatingPlayers(Arena.SpectatorType.PROFILELESS_AND_INACTIVE_PLAYER).size());
                        playerScoreboard.updateValue(getId(), Component.text(value, NamedTextColor.YELLOW));
                    }
                }
                case ARENA_SPECTATINGPLAYERS_PROFILELESS -> {
                    if (object instanceof Arena arena){
                        String value = String.valueOf(arena.getSpectatingPlayers(Arena.SpectatorType.PROFILELESS_PLAYER).size());
                        playerScoreboard.updateValue(getId(), Component.text(value, NamedTextColor.YELLOW));
                    }
                }
                case ARENA_TIMELEFT -> {
                    String timeLeftFormatted = (String) object;
                    playerScoreboard.updateValue(getId(), Component.text(timeLeftFormatted, NamedTextColor.YELLOW));
                }
            }
        }
    }
}
