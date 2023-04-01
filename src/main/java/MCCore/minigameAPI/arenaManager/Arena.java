package MCCore.minigameAPI.arenaManager;

import MCCore.Core;
import MCCore.events.GameStateChange;
import MCCore.minigameAPI.GameState;
import com.infernalsuite.aswm.api.world.SlimeWorld;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public abstract class Arena {

    int minPlayers = 0;
    int timer = 10;
    CountdownStyle countdownStyle = CountdownStyle.ACTIONBAR;
    String mode = "";
    SlimeWorld arenaWorld;
    Set<Location> spawnLocations = new HashSet<>();
    GameState gameState = GameState.STARTING;
    private final List<Player> players = new ArrayList<>();
    private final Countdown countdown = new Countdown(this);

    private int queueID;

    protected Arena(int queueID) {
        this.queueID = queueID;
    }

//Setters

    public void setMinimumPlayers(int minPlayers){
        this.minPlayers = minPlayers;
    }

    public void setTimer(int timer){
        this.timer = timer;
    }

    public void setMode(String mode){
        this.mode = mode;
    }

    public void setCountdownStyle(CountdownStyle countdownStyle){
        this.countdownStyle = countdownStyle;
    }

    public void setGameState(GameState gamestate){
        this.gameState = gamestate;
        new GameStateChange(this, gameState);
        if (gamestate == GameState.PLAYING){
            String[] out = new String[]{"minigameapi:startarena"
                    , String.valueOf(queueID)};
            Core.getClient().sendMessage(out);
        }
        if (gamestate == GameState.ENDING){
            ArenaManager.deleteArena(this);
        }
    }

    public void setArenaWorld(SlimeWorld world){
        this.arenaWorld = world;
    }

//Getters

    public int getMinPlayers(){
        return minPlayers;
    }

    public int getTimer(){
        return timer;
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

    public List<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player p){
        //new BukkitRunnable(){
            //public void run(){
                if (players.contains(p)) return;
                players.add(p);
                p.getInventory().clear();
                p.setLevel(0);
                p.setExp(0);
                p.resetTitle();
                p.resetCooldown();
                p.setArrowsInBody(0);
                Collection<PotionEffect> potions = p.getActivePotionEffects();
                for (PotionEffect effect : potions){
                    p.removePotionEffect(effect.getType());
                }
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 99999, 0));
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 99999, 0));
                p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 99999, 255));
                p.teleport(Core.getInstance().getMinigameWaitingWorld().getSpawnLocation());
            //}
       // }.runTaskLater(Core.getInstance(), 10);
    }

    public void removePlayer(Player p){
        players.remove(p);
        Collection<PotionEffect> potions = p.getActivePotionEffects();
        for (PotionEffect effect : potions){
            p.removePotionEffect(effect.getType());
        }
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
        countdown.start(countdownStyle, timer);
    }
}
