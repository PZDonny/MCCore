package net.donnypz.mccore.minigame.arena;

import net.donnypz.mccore.Core;
import net.donnypz.mccore.utils.ui.BossBarUtils;
import net.donnypz.mccore.utils.ui.scoreboard.PlayerScoreboard;
import net.donnypz.mccore.utils.ui.scoreboard.ScoreboardUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.TitlePart;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public abstract class ArenaContainer {
    protected static final HashMap<Arena, ArenaContainer> allContainedArenas = new HashMap<>();

    private Set<UUID> disconnectedEarly = new HashSet<>();
    protected final Arena arena;

    private int gameDuration = 0;

    private int timeLeft;

    protected String mapName = ChatColor.GRAY+"Unknown";

    private boolean isStarted = false;
    final Map<UUID, MinigamePlayerProfile> profiles = new HashMap<>();
    private ArenaContainer parentContainer;
    private ArenaContainer childContainer;


    /**
     * Create a child arena container of a different arena container
     * @param parentContainer the parent of this new child arena container.
     * @param gameDurationInSeconds
     */
    public ArenaContainer(ArenaContainer parentContainer, int gameDurationInSeconds){
        this.parentContainer = parentContainer;
        this.arena = parentContainer.arena;
        gameDuration = gameDurationInSeconds;
        timeLeft = gameDuration;
        parentContainer.childContainer = this;
    }


    /**
     * Create a single or parent arena container to manage an arena
     * @param arena
     * @param gameDurationInSeconds
     */
    public ArenaContainer(Arena arena, int gameDurationInSeconds){
        this.arena = arena;
        gameDuration = gameDurationInSeconds;
        timeLeft = gameDuration;
        allContainedArenas.put(arena, this);
    }

    public Arena getArena() {
        return arena;
    }

    public void setGameDuration(int durationInSeconds, boolean overrideCurrentTimeLeft) {
        this.gameDuration = durationInSeconds;
        if (timeLeft > durationInSeconds || overrideCurrentTimeLeft){
            this.timeLeft = durationInSeconds;
        }
        String barID;
        if (arena.getArenaType() == Arena.ArenaType.BUKKITMANUAL){
            barID = arena.getManualBukkitWorld().getName();
        }
        else{
            barID = arena.getMainArenaWorld().getName();
        }
        KeyedBossBar bar = BossBarUtils.getBossBar(barID);
        if (bar != null){
            bar.setTitle(ChatColor.WHITE+"Time Remaining : "+ChatColor.AQUA+(int) (Math.ceil((double) gameDuration/60))+" Minutes");
        }

    }

    public String getMapName(){
        return mapName;
    }


    protected abstract void onPlayersSentToArena();

    public void countdown(int duration, int minPlayers, boolean timeRemainingBossBar){
        if (isStarted){
            return;
        }
        new BukkitRunnable(){
            int count = duration;
            public void run(){

                if (arena == null || arena.isEndingOrNotUsable() || isStarted){
                    cancel();
                    return;
                }
                if (arena.getPlayingPlayers().size() < minPlayers){
                    arena.endGame(0, 20*3);
                    for (Player p : arena.getArenaPlayers()){
                        p.sendTitlePart(TitlePart.TITLE, Component.empty());
                        p.sendTitlePart(TitlePart.SUBTITLE, Component.text("Game Cancelled!", NamedTextColor.RED));
                        p.sendMessage(Component.text("Game Cancelled | Not enough players", NamedTextColor.RED));
                        p.playSound(p, Sound.BLOCK_BEACON_DEACTIVATE, 1, 1.5f);
                    }
                    cancel();
                    return;
                }
                if (count == 0){
                    startMatchTimer();
                    isStarted = true;
                    if (timeRemainingBossBar){
                        String barID;
                        if (arena.getArenaType() == Arena.ArenaType.BUKKITMANUAL){
                            barID = arena.getManualBukkitWorld().getName();
                        }
                        else{
                            barID = arena.getMainArenaWorld().getName();
                        }
                        String message = ChatColor.WHITE+"Time Remaining : "+ChatColor.AQUA+(int) (Math.ceil((double) gameDuration/60))+" Minutes";
                        BossBarUtils.sendBossBarTimed(arena.getPlayingPlayers(), barID, message, BarColor.BLUE, BarStyle.SOLID, gameDuration);
                    }
                    onCountdownEnd();
                    cancel();
                    return;
                }

                else{
                    for (Player p : arena.getArenaPlayers()){
                        p.sendTitlePart(TitlePart.TITLE, Component.text(ChatColor.AQUA+String.valueOf(count)));
                        p.sendTitlePart(TitlePart.SUBTITLE, Component.text(ChatColor.GRAY+"second(s) till match begins..."));
                        if (count > 2){
                            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
                        }
                        else if (count == 2){
                            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1.5f);
                        }
                        else if (count == 1){
                            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 2);
                        }
                    }
                }
                count--;
            }
        }.runTaskTimer(Core.getInstance(), 10, 20);

        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
            if (arena == null || arena.isEndingOrNotUsable()){
                return;
            }
            for (Player p : arena.getArenaPlayers()){
                sendGameInfoMessage(p);
            }
        }, 50);
    }

    protected abstract void onCountdownEnd();

    protected abstract void sendGameInfoMessage(Player player);

    private void startMatchTimer(){
        if (isStarted){
            return;
        }
        new BukkitRunnable(){
            public void run(){
                if (arena.isEndingOrNotUsable() || timeLeft < 0){
                    cancel();
                    return;
                }
                onMatchTimeLeftChange();
                for (Player p : arena.getArenaPlayers()){
                    PlayerScoreboard.UpdatingValue.ARENA_TIMELEFT.updateValue(ScoreboardUtils.getPlayerScoreboard(p), getTimeLeftFormatted());
                }
                timeLeft--;
            }
        }.runTaskTimer(Core.getInstance(), 0, 20);
    }

    public boolean isStarted() {
        return isStarted;
    }

    protected abstract void onMatchTimeLeftChange();

    public int getTimeLeftInSeconds() {
        return timeLeft;
    }

    public String getTimeLeftFormatted(){
        int minutes = (int) Math.floor((double) timeLeft/60);
        int seconds = timeLeft % 60;
        if (seconds < 10){
            return minutes+":0"+seconds;
        }
        return minutes+":"+seconds;
    }




    public static ArenaContainer getArenaContainer(Arena arena){
        if (arena == null || !allContainedArenas.containsKey(arena)){
            return null;
        }

        return allContainedArenas.get(arena);
    }

    public static ArenaContainer getArenaContainer(Player p){
        return getArenaContainer(ArenaManager.getArenaOfPlayer(p));
    }


    public static <T> T getArenaContainer(Arena arena, Class<T> clazz){
        if (arena == null || !allContainedArenas.containsKey(arena)){
            return null;
        }
        ArenaContainer container = allContainedArenas.get(arena);
        if (clazz.isInstance(container)){
            return clazz.cast(container);
        }

        return null;
    }

    public static <T> T getArenaContainer(Player p, Class<T> clazz){
        return getArenaContainer(ArenaManager.getArenaOfPlayer(p), clazz);
    }

    public ArenaContainer getChildContainer() {
        return childContainer;
    }

    public ArenaContainer getParentContainer() {
        return parentContainer;
    }

    public <T> T getChildContainer(Class<T> clazz) {
        if (clazz.isInstance(childContainer)){
            return clazz.cast(childContainer);
        }
        return null;
    }

    public <T> T getParentContainer(Class<T> clazz) {
        if (clazz.isInstance(parentContainer)){
            return clazz.cast(parentContainer);
        }
        return null;
    }

    public boolean isChildContainer(){
        return parentContainer != null;
    }

    public boolean isParentContainer(){
        return childContainer != null;
    }

    void delete(){
        allContainedArenas.remove(arena);
        onArenaRemoval();
        deleteChildContainer();
        for (MinigamePlayerProfile profile : profiles.values()){
            profile.delete();
        }
        profiles.clear();
    }

    public void deleteChildContainer(){
        if (childContainer == null){
            return;
        }
        childContainer.onArenaRemoval();
        childContainer.parentContainer = null;
        for (MinigamePlayerProfile profile : childContainer.profiles.values()){
            profile.delete();
        }
        childContainer.profiles.clear();
        childContainer = null;

    }

    public MinigamePlayerProfile getPlayerProfile(OfflinePlayer player){
        return getPlayerProfile(player.getUniqueId());
    }

    public MinigamePlayerProfile getPlayerProfile(UUID uuid){
        return profiles.get(uuid);
    }

    public <T> T getPlayerProfile(OfflinePlayer player, Class<T> clazz){
        return getPlayerProfile(player.getUniqueId(), clazz);
    }

    public <T> T getPlayerProfile(UUID uuid, Class<T> clazz){
        MinigamePlayerProfile profile = profiles.get(uuid);
        if (profile == null){
            return null;
        }
        if (clazz.isInstance(profile)){
           return clazz.cast(profile);
        }
        else{
            return null;
        }
    }

    public Set<MinigamePlayerProfile> getPlayerProfiles() {
        return new HashSet<>(profiles.values());
    }

    public <T> Set<T> getPlayerProfiles(Class<T> clazz) {
        Set<T> list = new HashSet<>();
        for (MinigamePlayerProfile profile : profiles.values()){
            if (clazz.isInstance(profile)){
                list.add(clazz.cast(profile));
            }
        }
        return list;
    }

    public Set<MinigamePlayerProfile> getActiveProfiles(){
        Set<MinigamePlayerProfile> active = new HashSet<>();
        profiles.values().forEach(profile -> {
            if (profile.isActive()){
                active.add(profile);
            }
        });
        return active;
    }

    public <T> Set<T> getActiveProfiles(Class<T> clazz){
        Set<T> active = new HashSet<>();
        profiles.values().forEach(profile -> {
            if (profile.isActive() && clazz.isInstance(profile)){
                active.add(clazz.cast(profile));
            }
        });
        return active;
    }

    public void removeProfile(OfflinePlayer player){
        removeProfile(player.getUniqueId());
    }

    public void removeProfile(UUID uuid){
        profiles.remove(uuid);
    }

    void addEarlyDisconnectPlayer(UUID uuid){
        disconnectedEarly.add(uuid);
    }

    void removeEarlyDisconnectPlayer(UUID uuid){
        disconnectedEarly.remove(uuid);
    }

    public boolean leftBeforeEndingState(OfflinePlayer player){
        return leftBeforeEndingState(player.getUniqueId());
    }

    public boolean leftBeforeEndingState(UUID uuid){
        return disconnectedEarly.contains(uuid);
    }

    protected abstract void onArenaRemoval();
}
