package MCCore.minigameAPI.arenaManager;

import MCCore.Core;
import MCCore.utils.BossBarTools;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.TitlePart;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public abstract class ArenaContainer {
    protected static final HashMap<Arena, ArenaContainer> allContainedArenas = new HashMap<>();


    protected final Arena arena;

    private int gameDuration;

    private int timeLeft;

    protected String mapName = ChatColor.GRAY+"Unknown";

    private boolean isStarted = false;
    final HashMap<UUID, MinigamePlayerProfile> profiles = new HashMap<>();


    public ArenaContainer(Arena arena, int gameDurationInSeconds){
        this.arena = arena;
        gameDuration = gameDurationInSeconds;
        timeLeft = gameDuration;
        allContainedArenas.put(arena, this);
    }

    public Arena getArena() {
        return arena;
    }

    public void setGameDuration(int durationInSeconds) {
        this.gameDuration = durationInSeconds;
        if (timeLeft > durationInSeconds){
            this.timeLeft = durationInSeconds;
        }
        String barID;
        if (arena.isManualWorld()){
            barID = arena.getManualWorld().getName();
        }
        else{
            barID = arena.getArenaWorld().getName();
        }
        KeyedBossBar bar = BossBarTools.getBossBar(barID);
        if (bar != null){
            bar.setTitle(ChatColor.WHITE+"Time Remaining : "+ChatColor.AQUA+(int) (Math.ceil((double) gameDuration/60))+" Minutes");
        }

    }

    public String getMapName(){
        return mapName;
    }


    public abstract void teleportPlayers();

    public void countdown(int duration, int minPlayers, boolean sendTimeRemaining){
        new BukkitRunnable(){
            int count = duration;
            public void run(){
                if (arena == null || arena.isEndingOrNotUsable()){
                    cancel();
                    return;
                }
                if (arena.getPlayingPlayers().size() < minPlayers){
                    arena.endGame(0, 20*5);
                    for (Player p : arena.getPlayingPlayers()){
                        p.sendTitlePart(TitlePart.TITLE, Component.empty());
                        p.sendTitlePart(TitlePart.SUBTITLE, Component.text(ChatColor.RED+"Game Cancelled!"));
                        p.playSound(p, Sound.BLOCK_BEACON_DEACTIVATE, 1, 1.5f);
                    }
                    cancel();
                    return;
                }
                if (count <= 0){
                    startMatchTimer();
                    isStarted = true;
                    if (sendTimeRemaining){
                        String barID;
                        if (arena.isManualWorld()){
                            barID = arena.getManualWorld().getName();
                        }
                        else{
                            barID = arena.getArenaWorld().getName();
                        }
                        String message = ChatColor.WHITE+"Time Remaining : "+ChatColor.AQUA+(int) (Math.ceil((double) gameDuration/60))+" Minutes";
                        BossBarTools.sendBossBarTimed(arena.getPlayingPlayers(), barID, message, BarColor.BLUE, BarStyle.SOLID, gameDuration);
                    }
                    onGameStart();

                    cancel();
                    return;
                }

                else{
                    for (Player p : arena.getPlayingPlayers()){
                        p.sendTitlePart(TitlePart.TITLE, Component.text(ChatColor.AQUA+String.valueOf(count)));
                        p.sendTitlePart(TitlePart.SUBTITLE, Component.text(ChatColor.YELLOW+"second(s) till game begins..."));
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
        new BukkitRunnable(){
            @Override
            public void run() {
                if (arena == null || arena.isEndingOrNotUsable()){
                    cancel();
                    return;
                }
                for (Player p : arena.getArenaPlayers()){
                    sendGameInfoMessage(p);
                }
            }
        }.runTaskLater(Core.getInstance(), 40);
    }

    protected abstract void onGameStart();

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



    public void removeArena(){
        allContainedArenas.remove(arena);
        deleteArena();
    }

    public MinigamePlayerProfile getPlayerProfile(OfflinePlayer player){
        return getPlayerProfile(player.getUniqueId());
    }

    public MinigamePlayerProfile getPlayerProfile(UUID uuid){
        return profiles.get(uuid);
    }


    protected abstract void deleteArena();

}
