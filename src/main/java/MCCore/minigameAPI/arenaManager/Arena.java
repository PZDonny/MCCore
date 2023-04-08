package MCCore.minigameAPI.arenaManager;

import MCCore.Core;
import MCCore.events.GameStateChangeEvent;
import MCCore.events.PlayerRemovedFromArenaEvent;
import MCCore.minigameAPI.GameState;
import MCCore.utils.Items;
import MCCore.utils.SlimeTools;
import com.infernalsuite.aswm.api.exceptions.WorldAlreadyExistsException;
import com.infernalsuite.aswm.api.world.SlimeWorld;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.*;

public class Arena {

    public static final ItemStack leaveTool = new Items().makeItem(Material.HOPPER_MINECART, 1, ChatColor.RED+"Right Click to leave this Queue!");

    int minPlayers = 0;
    int countdownDuration = 10;
    CountdownStyle countdownStyle = CountdownStyle.ACTIONBAR;
    String mode = "";
    SlimeWorld arenaWorld;

    String templateWorldName;
    GameState gameState = GameState.STARTING;
    private final List<Player> allPlayers = new ArrayList<>();

    private Set<Player> startPlayers = new HashSet<>();
    private Set<Player> playingPlayers = new HashSet<>();
    private Set<Player> spectatingPlayers = new HashSet<>();
    private final Countdown countdown = new Countdown(this);

    private int queueID;


//Constructor
    public Arena(int queueID) {
        this.queueID = queueID;
    }

//Setters
    public void setMinimumPlayers(int minPlayers){
        this.minPlayers = minPlayers;
    }

    public void setCountdownDuration(int duration){
        this.countdownDuration = duration;
    }

    public void setMode(String mode){
        this.mode = mode;
    }

    public void setCountdownStyle(CountdownStyle countdownStyle){
        this.countdownStyle = countdownStyle;
    }

    protected void setStateToPlaying(){
        GameState pastGameState = this.gameState;
        this.gameState = GameState.PLAYING;
        Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(this, GameState.PLAYING, pastGameState));

        if (gameState == GameState.PLAYING){
            String[] out = new String[]{"minigameapi:startarena"
                    , String.valueOf(queueID)};
            Core.getClient().sendMessage(out);
            for (Player p : allPlayers){
                ArenaManager.refreshPlayer(p);
            }
        }
    }

    public void endGame(int arenaDeletionDelayTicks){
        if (this.gameState == GameState.ENDING){
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"Failed to end game while in \"ENDING\" Gamestate! "+ChatColor.WHITE+"("+queueID+")");
            return;
        }

        GameState pastGameState = this.gameState;
        this.gameState = GameState.ENDING;
        Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(this, GameState.ENDING, pastGameState));
        Arena arena = this;
        new BukkitRunnable(){
            public void run(){
                ArenaManager.deleteArena(arena, null);
            }
        }.runTaskLater(Core.getInstance(), arenaDeletionDelayTicks);

    }


    public void generateArenaWorld(SlimeWorld world){
        try{
            SlimeWorld worldCloned = world.clone(world.getName()+"_"+ queueID, null);
            SlimeTools.getSlimePlugin().loadWorld(worldCloned);
            ArenaManager.getActiveArenas().put(worldCloned, this);
            this.arenaWorld = world;
            this.templateWorldName = world.getName();
        } catch (IOException | WorldAlreadyExistsException e) {
            throw new RuntimeException(e);
        }

    }


    public void setPlayingPlayers(List<Player> players){
        this.playingPlayers.addAll(players);
        this.startPlayers.addAll(players);
    }

    public void setSpectatingPlayers(Set<Player> players){
        this.spectatingPlayers = players;
    }

//Getters

    public String getTemplateWorldName() {
        return templateWorldName;
    }

    public int getMinPlayers(){
        return minPlayers;
    }

    public int getCountdownDuration(){
        return countdownDuration;
    }

    public String getMode(){
        return mode;
    }

    public CountdownStyle getCountdownStyle(){
        return countdownStyle;
    }

    public SlimeWorld getArenaWorld(){
        return arenaWorld;
    }

    public GameState getGameState(){
        return gameState;
    }

    public List<Player> getAllPlayers() {
        return allPlayers;
    }

    public Set<Player> getPlayingPlayers() {
        return playingPlayers;
    }
    public Set<Player> getStartPlayers() {
        return startPlayers;
    }

    public Set<Player> getSpectatingPlayers() {
        return spectatingPlayers;
    }

    public void makePlayerSpectate(Player p){
        playingPlayers.remove(p);
        if (startPlayers.contains(p)) spectatingPlayers.add(p);
    }

    public void addPlayer(Player p){
        if (allPlayers.contains(p)) return;
        allPlayers.add(p);
        ArenaManager.refreshPlayer(p);
        p.getInventory().setItem(4, leaveTool);
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 99999, 0));
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 99999, 0));
        p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 99999, 255));
        p.teleport(Core.getInstance().getMinigameWaitingWorld().getSpawnLocation());
    }

    public void removePlayer(Player p){
        allPlayers.remove(p);
        playingPlayers.remove(p);
        spectatingPlayers.remove(p);
        ArenaManager.refreshPlayer(p);
        Bukkit.getPluginManager().callEvent(new PlayerRemovedFromArenaEvent(this, p));
    }

    public Countdown getCountdown() {
        return countdown;
    }

    public int getQueueID() {
        return queueID;
    }

    public void deleteArena(){
        queueID = 0;
    }

    protected void doCountdown(){
        countdown.start(countdownStyle, countdownDuration);
    }
}
