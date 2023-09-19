package MCCore.minigameAPI.arenaManager;

import MCCore.Core;
import MCCore.events.ArenaWorldGeneratedEvent;
import MCCore.events.GameStateChangeEvent;
import MCCore.events.PlayerRemovedFromArenaEvent;
import MCCore.minigameAPI.GameState;
import MCCore.sockets.Messages;
import MCCore.utils.AbilityHandler;
import MCCore.utils.Items;
import MCCore.utils.Scoreboard.PlayerScoreboard;
import MCCore.utils.Scoreboard.ScoreboardUtils;
import MCCore.utils.SlimeTools;
import MCCore.utils.WorldTools;
import com.comphenix.protocol.PacketType;
import com.infernalsuite.aswm.api.world.SlimeWorld;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class Arena {
    public static final ItemStack leaveTool = Items.makeItem(Material.HOPPER_MINECART, 1, ChatColor.RED+"Return To Lobby"+ChatColor.YELLOW+" (Click)");
    public static final ItemStack requeueTool = Items.makeItem(Material.EMERALD, 1, ChatColor.GREEN+"Play Again"+ChatColor.YELLOW+" (Click)");

    int minPlayers = 0;
    int countdownDuration = 10;
    CountdownStyle countdownStyle = CountdownStyle.BOSSBAR;
    String minigameName = "";
    String mode = "";
    SlimeWorld arenaWorld;

    String templateWorldName;
    GameState gameState = GameState.STARTING;
    private final List<OfflinePlayer> startPlayers = new ArrayList<>();

    private final List<Player> playingPlayers = new ArrayList<>();

    private final HashSet<Player> spectators = new HashSet<>();
    private final ArenaCountdown countdown = new ArenaCountdown(this);

    private final HashSet<Scoreboard> scoreboards = new HashSet<>();

    private int queueID;

    private OfflinePlayer host;

    private boolean isInEndingProcess = false;

    private boolean allowSpectatorInteraction = false;

    private boolean isUsuable = true;


//Constructor
    public Arena(int queueID, OfflinePlayer host) {
        this.queueID = queueID;
        this.host = host;
    }

//Setters

    public void setAllowSpectatorInteraction(boolean status){
        this.allowSpectatorInteraction = status;
    }
    public void setMinimumPlayers(int minPlayers){
        this.minPlayers = minPlayers;
    }

    public void setCountdownDuration(int duration){
        this.countdownDuration = duration;
    }

    public void setMinigame(String minigameName, String mode){
        this.minigameName = minigameName;
        this.mode = mode;
    }

    public void setCountdownStyle(CountdownStyle countdownStyle){
        this.countdownStyle = countdownStyle;
    }
    public void storeBoard(Scoreboard scoreboard){
        scoreboards.add(scoreboard);
    }

    public void unstoreBoard(Scoreboard scoreboard){
        scoreboards.remove(scoreboard);
    }

    protected void setStateToPlaying(){
        GameState pastGameState = this.gameState;
        this.gameState = GameState.PLAYING;
        String[] out = new String[]{Messages.MINIGAMEAPI_STARTARENA.getID()
                , String.valueOf(queueID)};
        Core.getClient().sendMessage(out);
        for (OfflinePlayer p : startPlayers){
            if (p.isOnline()){
                ArenaManager.refreshPlayer((Player) p, GameMode.ADVENTURE);
                playingPlayers.add((Player) p);
            }
        }

        Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(this, GameState.PLAYING, pastGameState));
    }

    public void endGame(int endingStateDelay, int arenaDeletionDelayTicks){
        if (this.gameState == GameState.ENDING || isInEndingProcess){
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"Failed to end game while in \"ENDING\" Gamestate! "+ChatColor.WHITE+"("+queueID+")");
            return;
        }
        isInEndingProcess = true;

        Arena arena = this;

        new BukkitRunnable(){
            public void run(){
                GameState pastGameState = arena.gameState;
                arena.gameState = GameState.ENDING;
                Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(arena, GameState.ENDING, pastGameState));
            }
        }.runTaskLater(Core.getInstance(), endingStateDelay);


        new BukkitRunnable(){
            public void run(){
                ArenaManager.deleteArena(arena, null);
            }
        }.runTaskLater(Core.getInstance(), arenaDeletionDelayTicks);

    }

    public void setWorldDifficulty(Difficulty difficulty){
        if (arenaWorld == null) return;
        World bukkitWorld = Bukkit.getWorld(arenaWorld.getName());
        if (bukkitWorld != null){
            bukkitWorld.setDifficulty(difficulty);
        }

    }

    public void generateArenaWorld(String worldName, boolean autoGameRules){
        Arena arena = this;
        new BukkitRunnable(){
            @Override
            public void run() {
                SlimeWorld worldCloned = SlimeTools.getCloneFromDataSource(worldName, worldName+"_"+queueID, true);
                ArenaManager.addArena(worldCloned, arena);
                arena.arenaWorld = worldCloned;
                arena.templateWorldName = worldName;

                new BukkitRunnable(){
                    final int maxAttempts = 50;
                    int attempt = 0;
                    public void run() {
                        World bukkitWorld = Bukkit.getWorld(worldCloned.getName());
                        if (attempt >= maxAttempts) {
                            cancel();
                            return;
                        }
                        if (bukkitWorld != null) {
                            //Call WorldLoadEvent to prevent WorldGuard "Region data for WorldGuard failed to load for this world" error
                            new WorldLoadEvent(bukkitWorld).callEvent();
                            new ArenaWorldGeneratedEvent(arena).callEvent();
                            bukkitWorld.setAutoSave(false);
                            bukkitWorld.setKeepSpawnInMemory(false);
                            for (Chunk chunk : bukkitWorld.getLoadedChunks()){
                                chunk.setForceLoaded(false);
                            }
                            if (autoGameRules) {
                                bukkitWorld.setGameRule(GameRule.DISABLE_RAIDS, true);
                                bukkitWorld.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
                                bukkitWorld.setGameRule(GameRule.DO_INSOMNIA, false);
                                bukkitWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                                bukkitWorld.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
                                bukkitWorld.setGameRule(GameRule.MOB_GRIEFING, false);
                                bukkitWorld.setGameRule(GameRule.DO_MOB_SPAWNING, false);
                                bukkitWorld.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
                                bukkitWorld.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
                                bukkitWorld.setGameRule(GameRule.SPAWN_RADIUS, 0);
                                bukkitWorld.setGameRule(GameRule.DO_FIRE_TICK, false);
                                bukkitWorld.setGameRule(GameRule.FORGIVE_DEAD_PLAYERS, true);
                                bukkitWorld.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
                                bukkitWorld.setDifficulty(Difficulty.NORMAL);
                            }
                            cancel();
                        }
                        attempt++;
                    }
                }.runTaskTimer(Core.getInstance(), 15, 2);
            }
        }.runTaskAsynchronously(Core.getInstance());

    }


//Getters

    public boolean isSpectatorInteractionAllowed() {
        return allowSpectatorInteraction;
    }

    public String getTemplateWorldName() {
        return templateWorldName;
    }

    public int getMinPlayers(){
        return minPlayers;
    }

    public int getCountdownDuration(){
        return countdownDuration;
    }

    public String getMinigameName() {
        return minigameName;
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

    public List<OfflinePlayer> getStartPlayers() {
        return startPlayers;
    }

    public List<Player> getOnlineStartPlayers(){
        List<Player> players = new ArrayList<>();
        for (OfflinePlayer p : startPlayers){
            if (p.isOnline()){
                players.add((Player) p);
            }
        }
        return players;
    }

    public List<Player> getPlayingPlayers() {
        return new ArrayList<>(playingPlayers);
    }

    public Set<Player> getSpectatingPlayers() {
        return spectators;
    }

    public Set<Player> getArenaPlayers(){
        Set<Player> players = new HashSet<>();
        players.addAll(spectators);
        players.addAll(playingPlayers);
        return players;
    }

    public HashSet<Scoreboard> getScoreboards() {
        return scoreboards;
    }

    public boolean isPlayerSpectating(Player p){
        if (spectators.contains(p)) return true;
        else{
            MinigamePlayerProfile profile = MinigamePlayerProfile.getPlayerProfile(p);
            if (profile != null){
                return profile.isRespawning;
            }
        }
        return false;
    }

    public void makePlayerSpectate(Player p){
        playingPlayers.remove(p);
        p.closeInventory();
        p.setShoulderEntityLeft(null);
        p.setShoulderEntityRight(null);
        p.setWalkSpeed(0.2f);
        p.setFlySpeed(0.1f);
        p.setInvulnerable(true);
        p.setAllowFlight(true);
        p.setFlying(true);
        p.setGameMode(GameMode.ADVENTURE);
        p.getInventory().clear();
        p.setCanPickupItems(false);
        p.getInventory().setItem(4, requeueTool);
        p.getInventory().setHeldItemSlot(0);
        Collection<PotionEffect> potions = p.getActivePotionEffects();
        for (PotionEffect effect : potions){
            p.removePotionEffect(effect.getType());
        }
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 0));
        p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, PotionEffect.INFINITE_DURATION, 0));
    //Hide Player from Playing Players
        for (Player player : playingPlayers){
            player.hidePlayer(Core.getInstance(), p);
        }
    //Reveal Spectators to Player
        for (Player spectator : spectators){
            if (spectator.isOnline()) p.showPlayer(Core.getInstance(), spectator);
        }
        spectators.add(p);
        for (Player o : getArenaPlayers()){
            PlayerScoreboard board = ScoreboardUtils.getPlayerScoreboard(o);
            PlayerScoreboard.UpdatingValue.ARENA_PLAYINGPLAYERS.updateValue(board, this);
            PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS.updateValue(board, this);
        }
    }

    public void makePlayerTempSpectate(Player p){
        MinigamePlayerProfile profile = MinigamePlayerProfile.getPlayerProfile(p);
        if (profile.isRespawning()) return;
        p.setShoulderEntityLeft(null);
        p.setShoulderEntityRight(null);
        p.closeInventory();
        p.setWalkSpeed(0.2f);
        p.setFlySpeed(0.1f);
        p.setInvulnerable(true);
        p.setGameMode(GameMode.ADVENTURE);
        p.setCanPickupItems(false);
        profile.setRespawningState(true);
        Collection<PotionEffect> potions = p.getActivePotionEffects();
        for (PotionEffect effect : potions){
            p.removePotionEffect(effect.getType());
        }
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 0));
        p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, PotionEffect.INFINITE_DURATION, 0));
        for (Player player : playingPlayers){
            if (player != p) player.hidePlayer(Core.getInstance(), p);
        }
    }

    public void revivePlayer(Player p, GameMode gameMode){
        MinigamePlayerProfile profile = MinigamePlayerProfile.getPlayerProfile(p);
        if (!profile.isRespawning()) return;
        p.closeInventory();
        p.setInvulnerable(false);
        p.setGameMode(gameMode);
        p.setCanPickupItems(true);
        profile.setRespawningState(true);
        Collection<PotionEffect> potions = p.getActivePotionEffects();
        for (PotionEffect effect : potions){
            p.removePotionEffect(effect.getType());
        }
        for (Player player : playingPlayers){
            if (player != p) player.showPlayer(Core.getInstance(), p);
        }
    }

    public void addPlayer(Player p){
        if (startPlayers.contains(p)) return;
        startPlayers.add(p);
        ArenaManager.refreshPlayer(p, GameMode.ADVENTURE);
        for (OfflinePlayer o : startPlayers){
            if (o.isOnline()){
                Player oPlayer = (Player) o;
                oPlayer.showPlayer(Core.getInstance(), p);
                p.showPlayer(Core.getInstance(), oPlayer);
            }
        }
        p.getInventory().setHeldItemSlot(0);
        p.getInventory().setItem(4, leaveTool);
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 99999, 0));
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 99999, 0));
        p.teleport(Core.getInstance().getMinigameWaitingWorld().getSpawnLocation());
    }

    public void removePlayer(Player p, PlayerRemovedFromArenaEvent.RemoveCause cause){
        startPlayers.remove(p);
        playingPlayers.remove(p);
        spectators.remove(p);
        ArenaManager.refreshPlayer(p, GameMode.ADVENTURE);
        Bukkit.getPluginManager().callEvent(new PlayerRemovedFromArenaEvent(this, p, cause));
        for (Player o : getArenaPlayers()){
            PlayerScoreboard board = ScoreboardUtils.getPlayerScoreboard(o);
            PlayerScoreboard.UpdatingValue.ARENA_PLAYINGPLAYERS.updateValue(board, this);
            PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS.updateValue(board, this);
        }

    }

    public ArenaCountdown getCountdown() {
        return countdown;
    }

    public int getQueueID() {
        return queueID;
    }

    public boolean isInEndingProcess() {
        return isInEndingProcess;
    }

    public OfflinePlayer getHost(){
        return host;
    }

    public boolean isPrivate(){
        return host != null;
    }

    void deleteArena(){
        queueID = 0;
    //Delete Stats
        for (OfflinePlayer p : startPlayers){
            MinigamePlayerProfile profile = MinigamePlayerProfile.getPlayerProfile(p);
            if (profile != null){
                profile.deleteProfile();
            }
            AbilityHandler.removePlayerData(startPlayers);
        }
        playingPlayers.clear();
        startPlayers.clear();
        spectators.clear();
        host = null;
        isUsuable = false;
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD+"Removed slime world "+ ChatColor.AQUA+arenaWorld.getName()+ChatColor.GOLD+" from cache!");
        String arenaWorldName = arenaWorld.getName();
        arenaWorld = null;
        WorldTools.destroyWorld(Bukkit.getWorld(arenaWorldName));
    }

    public boolean isUsuable(){
        return isUsuable;
    }

    protected void doCountdown(){
        countdown.start(countdownStyle, countdownDuration);
    }
}
